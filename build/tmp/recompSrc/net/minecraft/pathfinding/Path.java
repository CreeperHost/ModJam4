package net.minecraft.pathfinding;

public class Path
{
    /** Contains the points in this path */
    private PathPoint[] pathPoints = new PathPoint[1024];
    /** The number of points in this path */
    private int count;
    private static final String __OBFID = "CL_00000573";

    /**
     * Adds a point to the path
     */
    public PathPoint addPoint(PathPoint par1PathPoint)
    {
        if (par1PathPoint.index >= 0)
        {
            throw new IllegalStateException("OW KNOWS!");
        }
        else
        {
            if (this.count == this.pathPoints.length)
            {
                PathPoint[] apathpoint = new PathPoint[this.count << 1];
                System.arraycopy(this.pathPoints, 0, apathpoint, 0, this.count);
                this.pathPoints = apathpoint;
            }

            this.pathPoints[this.count] = par1PathPoint;
            par1PathPoint.index = this.count;
            this.sortBack(this.count++);
            return par1PathPoint;
        }
    }

    /**
     * Clears the path
     */
    public void clearPath()
    {
        this.count = 0;
    }

    /**
     * Returns and removes the first point in the path
     */
    public PathPoint dequeue()
    {
        PathPoint pathpoint = this.pathPoints[0];
        this.pathPoints[0] = this.pathPoints[--this.count];
        this.pathPoints[this.count] = null;

        if (this.count > 0)
        {
            this.sortForward(0);
        }

        pathpoint.index = -1;
        return pathpoint;
    }

    /**
     * Changes the provided point's distance to target
     */
    public void changeDistance(PathPoint par1PathPoint, float par2)
    {
        float f1 = par1PathPoint.distanceToTarget;
        par1PathPoint.distanceToTarget = par2;

        if (par2 < f1)
        {
            this.sortBack(par1PathPoint.index);
        }
        else
        {
            this.sortForward(par1PathPoint.index);
        }
    }

    /**
     * Sorts a point to the left
     */
    private void sortBack(int par1)
    {
        PathPoint pathpoint = this.pathPoints[par1];
        int j;

        for (float f = pathpoint.distanceToTarget; par1 > 0; par1 = j)
        {
            j = par1 - 1 >> 1;
            PathPoint pathpoint1 = this.pathPoints[j];

            if (f >= pathpoint1.distanceToTarget)
            {
                break;
            }

            this.pathPoints[par1] = pathpoint1;
            pathpoint1.index = par1;
        }

        this.pathPoints[par1] = pathpoint;
        pathpoint.index = par1;
    }

    /**
     * Sorts a point to the right
     */
    private void sortForward(int par1)
    {
        PathPoint pathpoint = this.pathPoints[par1];
        float f = pathpoint.distanceToTarget;

        while (true)
        {
            int j = 1 + (par1 << 1);
            int k = j + 1;

            if (j >= this.count)
            {
                break;
            }

            PathPoint pathpoint1 = this.pathPoints[j];
            float f1 = pathpoint1.distanceToTarget;
            PathPoint pathpoint2;
            float f2;

            if (k >= this.count)
            {
                pathpoint2 = null;
                f2 = Float.POSITIVE_INFINITY;
            }
            else
            {
                pathpoint2 = this.pathPoints[k];
                f2 = pathpoint2.distanceToTarget;
            }

            if (f1 < f2)
            {
                if (f1 >= f)
                {
                    break;
                }

                this.pathPoints[par1] = pathpoint1;
                pathpoint1.index = par1;
                par1 = j;
            }
            else
            {
                if (f2 >= f)
                {
                    break;
                }

                this.pathPoints[par1] = pathpoint2;
                pathpoint2.index = par1;
                par1 = k;
            }
        }

        this.pathPoints[par1] = pathpoint;
        pathpoint.index = par1;
    }

    /**
     * Returns true if this path contains no points
     */
    public boolean isPathEmpty()
    {
        return this.count == 0;
    }
}