package tech.zeroed.pipes.systems;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import tech.zeroed.pipes.components.FillProgress;
import tech.zeroed.pipes.components.Source;

/**
 * Every update this system sets the capacity of all sources to double their max capacity
 * This will cause the FloodSystem to try and distribute this water throughout any connected pipes
 */
@All({Source.class, FillProgress.class})
public class SourceSystem extends IteratingSystem {
    public ComponentMapper<FillProgress> mFillProgress;

    @Override
    protected void process(int entityId) {
        FillProgress fillProgress = mFillProgress.get(entityId);
        fillProgress.setCurrent(fillProgress.maxCapacity*2);
    }
}
