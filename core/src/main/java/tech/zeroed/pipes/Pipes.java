package tech.zeroed.pipes;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import tech.zeroed.pipes.components.LevelComponent;
import tech.zeroed.pipes.components.UnloadedLevel;
import tech.zeroed.pipes.systems.*;
import tech.zeroed.pipes.systems.debug.FloodGridRenderer;
import tech.zeroed.pipes.systems.debug.PathRendererSystem;

import static tech.zeroed.pipes.Globals.MIN_DELTA;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Pipes extends ApplicationAdapter {
	private World world;
	private Stage stage;
	private InputMultiplexer multiplexer;

	@Override
	public void create() {
		stage = new Stage();
		multiplexer = new InputMultiplexer(stage);

		WorldConfiguration setup = new WorldConfigurationBuilder()
				.with(
						//new GridSystem(),
						new LevelManager(),
						new EntityCreationSystem(),
						new RenderSystem(),
						new WordCameraSystem(),
						new InputSystem(multiplexer),
						new SpawnerSystem(),
						new PathFindingSystem(),
						new PathFollowSystem(),
						new PathRendererSystem(),
						new SourceSystem(),
						new FloodSystem(),
						new FloodGridRenderer()
				)
				.build();
		world = new World(setup);

		// Load a test level;
		world.createEntity().edit().add(new LevelComponent().setLevelFile("Test.tmx")).add(new UnloadedLevel());
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		world.setDelta(MathUtils.clamp(Gdx.graphics.getDeltaTime(), 0, MIN_DELTA));
		world.process();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		world.getSystem(WordCameraSystem.class).resize(width, height);
	}
}