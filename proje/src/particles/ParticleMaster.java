package particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import renderEngine.Loader;

public class ParticleMaster {
	private static Map<ParticleTexture, List<Particle>> particles = new HashMap<ParticleTexture, List<Particle>>();
	private static Map<ParticleTexture, List<Particle>> deadParticles = new HashMap<ParticleTexture, List<Particle>>();
	private static ParticleRenderer renderer;

	public static float ppsCounter = 0;

	public static void init(Loader loader, Matrix4f projectionMatrix, float pps) {
		renderer = new ParticleRenderer(loader, projectionMatrix);
	}

	public static void getParticle(ParticleTexture texture, float pps, float gravityComplient, float lifeLength,
			float rotation, float scale, Vector3f velocity, Vector3f center) {

		List<Particle> list = deadParticles.get(texture);
		List<Particle> list2 = particles.get(texture);
		if (ppsCounter < pps) {
			ppsCounter++;
			new Particle(gravityComplient, lifeLength, rotation, scale, new Vector3f(center), velocity,
					texture);
		} else if (ppsCounter == pps) {
			if (list != null && !list.isEmpty()) {
				particleReset(list.get(0), center,velocity);
				list2.add(list.get(0));
				list.remove(0);
			}
		}
	}

	public static void update(Camera camera) {
		Iterator<Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();
		while (mapIterator.hasNext()) {
			List<Particle> list = mapIterator.next().getValue();
			Iterator<Particle> iterator = list.iterator();
			while (iterator.hasNext()) {
				Particle p = iterator.next();
				boolean stillAlive = p.update(camera);
				if (!stillAlive) {
					iterator.remove();
					killParticle(p);

				}
			}
			InsertionSort.sortHighToLow(list);
		}

	}

	private static void particleReset(Particle p, Vector3f center, Vector3f velocity) {
		p.positionReset(new Vector3f(center), new Vector3f(velocity));
	}

	public static void renderParticles(Camera camera) {
		renderer.render(particles, camera);
	}

	public static void cleanup() {
		renderer.cleanUp();
	}

	public static void killParticle(Particle p) {
		List<Particle> list = deadParticles.get(p.getTexture());
		List<Particle> list2 = particles.get(p.getTexture());
		if (list == null) {
			list = new ArrayList<Particle>();
			deadParticles.put(p.getTexture(), list);
		}
		list.add(p);
		list2.remove(p);
	}

	public static void addParticle(Particle p) {
		List<Particle> list = particles.get(p.getTexture());
		if (list == null) {
			list = new ArrayList<Particle>();
			particles.put(p.getTexture(), list);
		}
		list.add(p);
	}
}
