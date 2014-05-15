package net.minecraft.scoreboard;

import java.util.Comparator;
import java.util.List;

public class Score
{
    public static final Comparator field_96658_a = new Comparator()
    {
        private static final String __OBFID = "CL_00000618";
        public int compare(Score par1Score, Score par2Score)
        {
            return par1Score.getScorePoints() > par2Score.getScorePoints() ? 1 : (par1Score.getScorePoints() < par2Score.getScorePoints() ? -1 : 0);
        }
        public int compare(Object par1Obj, Object par2Obj)
        {
            return this.compare((Score)par1Obj, (Score)par2Obj);
        }
    };
    private final Scoreboard theScoreboard;
    private final ScoreObjective theScoreObjective;
    private final String scorePlayerName;
    private int field_96655_e;
    private static final String __OBFID = "CL_00000617";

    public Score(Scoreboard par1Scoreboard, ScoreObjective par2ScoreObjective, String par3Str)
    {
        this.theScoreboard = par1Scoreboard;
        this.theScoreObjective = par2ScoreObjective;
        this.scorePlayerName = par3Str;
    }

    public void increseScore(int par1)
    {
        if (this.theScoreObjective.getCriteria().isReadOnly())
        {
            throw new IllegalStateException("Cannot modify read-only score");
        }
        else
        {
            this.setScorePoints(this.getScorePoints() + par1);
        }
    }

    public void decreaseScore(int par1)
    {
        if (this.theScoreObjective.getCriteria().isReadOnly())
        {
            throw new IllegalStateException("Cannot modify read-only score");
        }
        else
        {
            this.setScorePoints(this.getScorePoints() - par1);
        }
    }

    public void func_96648_a()
    {
        if (this.theScoreObjective.getCriteria().isReadOnly())
        {
            throw new IllegalStateException("Cannot modify read-only score");
        }
        else
        {
            this.increseScore(1);
        }
    }

    public int getScorePoints()
    {
        return this.field_96655_e;
    }

    public void setScorePoints(int par1)
    {
        int j = this.field_96655_e;
        this.field_96655_e = par1;

        if (j != par1)
        {
            this.getScoreScoreboard().func_96536_a(this);
        }
    }

    public ScoreObjective func_96645_d()
    {
        return this.theScoreObjective;
    }

    public String getPlayerName()
    {
        return this.scorePlayerName;
    }

    public Scoreboard getScoreScoreboard()
    {
        return this.theScoreboard;
    }

    public void func_96651_a(List par1List)
    {
        this.setScorePoints(this.theScoreObjective.getCriteria().func_96635_a(par1List));
    }
}