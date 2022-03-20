package tech.zeroed.pipes.systems;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import tech.zeroed.pipes.components.Health;

@All(Health.class)
public class HealthSystem extends IteratingSystem {
    protected ComponentMapper<Health> mHealth;

    @Override
    protected void process(int entityId) {
        Gdx.app.log("", ""+mHealth.get(entityId).health);
        mHealth.get(entityId).health--;
        if(mHealth.get(entityId).health == 0){
            world.delete(entityId);
        }
    }
}
