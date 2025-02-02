package org.example;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.example.data.SpatialGrid;

import java.util.ArrayList;

public class Particle implements PhysicsObject {

	private Vector2 position;
	private int radius;
	private Vector3 farbe;
	private Vector2 geschwindigkeit;

	private float masse;
	private PhysicsObject[] andereObjekte;

	private Vector2 cell = null;

	// Gravitationskonstante
	private final float G = (float) (6.673*Math.pow(10.0, -11.0));

	/**
	 * @param position Position des Planeten
	 * @param radius Radius des Planeten
	 * @param farbe Farbe des Planeten(Rot, Gruen, Blau)
	 * @param geschwindigkeit Urspruengliche Geschwindigkeit des Planeten
	 * @param masse Masse des Planeten
	 */
	public Particle(Vector2 position, int radius, Vector3 farbe, Vector2 geschwindigkeit, float masse, PhysicsObject[] andereObjekte) {
		this.position = position;
		this.radius = radius;
		this.farbe = farbe;
		this.geschwindigkeit = geschwindigkeit;
		this.masse = masse;
		this.andereObjekte = andereObjekte;
	}
	/**
	 * @param position Position des Planeten
	 * @param radius Radius des Planeten
	 * @param farbe Farbe des Planeten(Rot, Gruen, Blau)
	 * @param geschwindigkeit Urspruengliche Geschwindigkeit des Planeten
	 * @param masse Masse des Planeten
	 */
	public Particle(Vector2 position, int radius, Vector3 farbe, Vector2 geschwindigkeit, float masse) {
		this.position = position;
		this.radius = radius;
		this.farbe = farbe;
		this.geschwindigkeit = geschwindigkeit;
		this.masse = masse;
		this.andereObjekte = null;
	}

	@Override
	public void zeichne(ShapeRenderer zeichner) {
		zeichner.setColor(new Color(this.farbe.x, this.farbe.y, this.farbe.z, 1.0f));
		zeichner.circle(this.position.x, this.position.y, this.radius);
		//Vector2 top = new Vector2(-10.0f, 0.0f).rotateRad(this.)
		//Vector2 bottom_left = this.position.cpy().add(1)
		//zeichner.triangle(this.position.x, this.position.y, );

		/*zeichner.setColor(new Color(0.0f, 1.0f, 0.0f, 1.0f));
		Vector2 bewegung = getProzedualeBewegung(0.016f).add(this.position);
		bewegung = bewegung.sub(this.position);
		bewegung.scl(10.0f);
		bewegung.add(this.position);
		zeichner.rectLine(this.position.x, this.position.y, bewegung.x, bewegung.y, 3.0f);*/
	}

	@Override
	public void setPosition(Vector2 position) {
		this.position = position;
	}

	@Override
	public void addPosition(Vector2 position) {
		this.position = this.position.add(position);
	}

	@Override
	public void bewege(Vector2 bewegung) {
		this.position.add(bewegung);
	}

	@Override
	public void wendeProzedualeBewegungAn(Vector2 bewegung) {
		bewege(bewegung);
		//System.out.printf("x: %f | y: %f\n", this.position.x, this.position.y);
	}

	private float getAngle(Vector2 g) {
		Vector2 f = new Vector2(0.0f, 1.0f);
		return getAngleBetween(f, g);
	}

	private float getAngleBetween(Vector2 f, Vector2 g) {
		float dot = Math.abs(f.dot(g));
		float lenProd = f.len()*g.len();
		float a = (float) Math.acos(dot/lenProd);
		//System.out.println(a);
		return a;
	}

	@Override
	public Vector2 getProzedualeBewegung(float deltaTime, SpatialGrid grid) {
		ArrayList<Particle>[] cells = grid.getCellsInRadius(200, this.position);

		// Beschleunigung relativ zu den anderen Himmelskoerpern berechnen
		for (int i = 0; i < cells.length; i++) {
			ArrayList<Particle> cell = cells[i];
			for (int j = 0; j < cell.size(); j++) {
				float dist = this.position.dst2(cell.get(j).getPosition());
				float m = this.masse * cell.get(j).getMasse();
				float fG = this.G * (m / dist);

				//Vector2 temp = new Vector2(0.0f, fG);
				Vector2 temp = new Vector2(0.0f, 0.0f);
				Vector2 diffVec = new Vector2(this.position).sub(cell.get(j).getPosition());
				Vector2 dir = new Vector2(0.0f, 1.0f);

				float x = diffVec.x;
				float y = diffVec.y;
				// I
				if (x >= 0 && y > 0) {
					dir = new Vector2(-1.0f, 0.0f);
					temp = new Vector2(-fG, 0.0f);
				}
				//II
				else if (x > 0 && y <= 0) {
					dir = new Vector2(0.0f, 1.0f);
					temp = new Vector2(0.0f, fG);
				}
				// III
				else if (x <= 0 && y < 0) {
					dir = new Vector2(1.0f, 0.0f);
					temp = new Vector2(fG, 0.0f);
				}
				// IV
				else if (x < 0 && y >= 0) {
					dir = new Vector2(0.0f, -1.0f);
					temp = new Vector2(0.0f, -fG);
				}
				diffVec = new Vector2(Math.abs(diffVec.x), Math.abs(diffVec.y));

				//if (diffVec.x < 0.0f) temp.scl(-1.0f);
				float winkel = getAngleBetween(dir, diffVec);
				//temp = new Vector2(Math.abs(temp.x*dir.x), Math.abs(temp.y*dir.y));
				temp.rotateRad(winkel);
				//Vector2 temp = new Vector2(this.andereObjekte[i].getPosition()).add(new Vector2(this.andereObjekte[i].getPosition()).sub(this.position)).scl(fG);
				this.geschwindigkeit.add(temp);
			}
		}

		// Bewgegung anwenden
		int xNeg = this.geschwindigkeit.x != Math.abs(this.geschwindigkeit.x) ? -1 : 1;
		int yNeg = this.geschwindigkeit.y != Math.abs(this.geschwindigkeit.y) ? -1 : 1;

		/*this.geschwindigkeit = new Vector2(
				(Math.abs(this.geschwindigkeit.x) % 1500) * xNeg,
				(Math.abs(this.geschwindigkeit.y) % 1500) * yNeg
		);*/
		Vector2 diff = new Vector2(this.geschwindigkeit);
		return diff.scl(deltaTime);
	}

	@Override
	public float getMasse() {
		return this.masse;
	}

	@Override
	public Vector2 getPosition() {
		return this.position;
	}

	@Override
	public String getGeschwindigkeitsLabel() {
		return String.format("vx: %.2f | vy: %.2f", this.geschwindigkeit.x, this.geschwindigkeit.y);
	}

	public void setAndereObjekte(PhysicsObject[] andereObjekte) {
		this.andereObjekte = andereObjekte;
	}

	public Vector2 getCell() {
		return cell;
	}

	public void setCell(Vector2 cell) {
		this.cell = cell;
	}
}
