package net.minecraft.entity.ai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAITasks
{
    private static final Logger logger = LogManager.getLogger();
    /** A list of EntityAITaskEntrys in EntityAITasks. */
    public List taskEntries = new ArrayList();
    /** A list of EntityAITaskEntrys that are currently being executed. */
    private List executingTaskEntries = new ArrayList();
    /** Instance of Profiler. */
    private final Profiler theProfiler;
    private int tickCount;
    private int tickRate = 3;
    private static final String __OBFID = "CL_00001588";

    public EntityAITasks(Profiler par1Profiler)
    {
        this.theProfiler = par1Profiler;
    }

    public void addTask(int par1, EntityAIBase par2EntityAIBase)
    {
        this.taskEntries.add(new EntityAITasks.EntityAITaskEntry(par1, par2EntityAIBase));
    }

    /**
     * removes the indicated task from the entity's AI tasks.
     */
    public void removeTask(EntityAIBase par1EntityAIBase)
    {
        Iterator iterator = this.taskEntries.iterator();

        while (iterator.hasNext())
        {
            EntityAITasks.EntityAITaskEntry entityaitaskentry = (EntityAITasks.EntityAITaskEntry)iterator.next();
            EntityAIBase entityaibase1 = entityaitaskentry.action;

            if (entityaibase1 == par1EntityAIBase)
            {
                if (this.executingTaskEntries.contains(entityaitaskentry))
                {
                    entityaibase1.resetTask();
                    this.executingTaskEntries.remove(entityaitaskentry);
                }

                iterator.remove();
            }
        }
    }

    public void onUpdateTasks()
    {
        ArrayList arraylist = new ArrayList();
        Iterator iterator;
        EntityAITasks.EntityAITaskEntry entityaitaskentry;

        if (this.tickCount++ % this.tickRate == 0)
        {
            iterator = this.taskEntries.iterator();

            while (iterator.hasNext())
            {
                entityaitaskentry = (EntityAITasks.EntityAITaskEntry)iterator.next();
                boolean flag = this.executingTaskEntries.contains(entityaitaskentry);

                if (flag)
                {
                    if (this.canUse(entityaitaskentry) && this.canContinue(entityaitaskentry))
                    {
                        continue;
                    }

                    entityaitaskentry.action.resetTask();
                    this.executingTaskEntries.remove(entityaitaskentry);
                }

                if (this.canUse(entityaitaskentry) && entityaitaskentry.action.shouldExecute())
                {
                    arraylist.add(entityaitaskentry);
                    this.executingTaskEntries.add(entityaitaskentry);
                }
            }
        }
        else
        {
            iterator = this.executingTaskEntries.iterator();

            while (iterator.hasNext())
            {
                entityaitaskentry = (EntityAITasks.EntityAITaskEntry)iterator.next();

                if (!entityaitaskentry.action.continueExecuting())
                {
                    entityaitaskentry.action.resetTask();
                    iterator.remove();
                }
            }
        }

        this.theProfiler.startSection("goalStart");
        iterator = arraylist.iterator();

        while (iterator.hasNext())
        {
            entityaitaskentry = (EntityAITasks.EntityAITaskEntry)iterator.next();
            this.theProfiler.startSection(entityaitaskentry.action.getClass().getSimpleName());
            entityaitaskentry.action.startExecuting();
            this.theProfiler.endSection();
        }

        this.theProfiler.endSection();
        this.theProfiler.startSection("goalTick");
        iterator = this.executingTaskEntries.iterator();

        while (iterator.hasNext())
        {
            entityaitaskentry = (EntityAITasks.EntityAITaskEntry)iterator.next();
            entityaitaskentry.action.updateTask();
        }

        this.theProfiler.endSection();
    }

    /**
     * Determine if a specific AI Task should continue being executed.
     */
    private boolean canContinue(EntityAITasks.EntityAITaskEntry par1EntityAITaskEntry)
    {
        this.theProfiler.startSection("canContinue");
        boolean flag = par1EntityAITaskEntry.action.continueExecuting();
        this.theProfiler.endSection();
        return flag;
    }

    /**
     * Determine if a specific AI Task can be executed, which means that all running higher (= lower int value) priority
     * tasks are compatible with it or all lower priority tasks can be interrupted.
     */
    private boolean canUse(EntityAITasks.EntityAITaskEntry par1EntityAITaskEntry)
    {
        this.theProfiler.startSection("canUse");
        Iterator iterator = this.taskEntries.iterator();

        while (iterator.hasNext())
        {
            EntityAITasks.EntityAITaskEntry entityaitaskentry = (EntityAITasks.EntityAITaskEntry)iterator.next();

            if (entityaitaskentry != par1EntityAITaskEntry)
            {
                if (par1EntityAITaskEntry.priority >= entityaitaskentry.priority)
                {
                    if (this.executingTaskEntries.contains(entityaitaskentry) && !this.areTasksCompatible(par1EntityAITaskEntry, entityaitaskentry))
                    {
                        this.theProfiler.endSection();
                        return false;
                    }
                }
                else if (this.executingTaskEntries.contains(entityaitaskentry) && !entityaitaskentry.action.isInterruptible())
                {
                    this.theProfiler.endSection();
                    return false;
                }
            }
        }

        this.theProfiler.endSection();
        return true;
    }

    /**
     * Returns whether two EntityAITaskEntries can be executed concurrently
     */
    private boolean areTasksCompatible(EntityAITasks.EntityAITaskEntry par1EntityAITaskEntry, EntityAITasks.EntityAITaskEntry par2EntityAITaskEntry)
    {
        return (par1EntityAITaskEntry.action.getMutexBits() & par2EntityAITaskEntry.action.getMutexBits()) == 0;
    }

    public class EntityAITaskEntry
    {
        /** The EntityAIBase object. */
        public EntityAIBase action;
        /** Priority of the EntityAIBase */
        public int priority;
        private static final String __OBFID = "CL_00001589";

        public EntityAITaskEntry(int par2, EntityAIBase par3EntityAIBase)
        {
            this.priority = par2;
            this.action = par3EntityAIBase;
        }
    }
}