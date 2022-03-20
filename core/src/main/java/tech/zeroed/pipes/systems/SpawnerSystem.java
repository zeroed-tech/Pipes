package tech.zeroed.pipes.systems;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import tech.zeroed.pipes.components.Position;
import tech.zeroed.pipes.components.Spawner;

@All({Spawner.class})
public class SpawnerSystem extends IteratingSystem {
    EntityCreationSystem entityCreationSystem;
    float timeTillNextSpawn = 0;

    ComponentMapper<Spawner> mSpawner;
    ComponentMapper<Position> mPosition;

    @Override
    protected void process(int entityId) {
        timeTillNextSpawn -= world.delta;
        if(timeTillNextSpawn <= 0){
            timeTillNextSpawn += mSpawner.get(entityId).spawnRate + MathUtils.random(-0.5f, 0.5f);
            Vector2 position = mPosition.get(entityId).getPosition();
        }
    }
}
