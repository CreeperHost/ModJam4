package net.minecraft.scoreboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Scoreboard
{
    /** Map of objective names to ScoreObjective objects. */
    private final Map scoreObjectives = new HashMap();
    private final Map scoreObjectiveCriterias = new HashMap();
    private final Map field_96544_c = new HashMap();
    private final ScoreObjective[] field_96541_d = new ScoreObjective[3];
    /** Map of teamnames to ScorePlayerTeam instances */
    private final Map teams = new HashMap();
    /** Map of usernames to ScorePlayerTeam objects. */
    private final Map teamMemberships = new HashMap();
    private static final String __OBFID = "CL_00000619";

    /**
     * Returns a ScoreObjective for the objective name
     */
    public ScoreObjective getObjective(String par1Str)
    {
        return (ScoreObjective)this.scoreObjectives.get(par1Str);
    }

    public ScoreObjective addScoreObjective(String par1Str, IScoreObjectiveCriteria par2ScoreObjectiveCriteria)
    {
        ScoreObjective scoreobjective = this.getObjective(par1Str);

        if (scoreobjective != null)
        {
            throw new IllegalArgumentException("An objective with the name \'" + par1Str + "\' already exists!");
        }
        else
        {
            scoreobjective = new ScoreObjective(this, par1Str, par2ScoreObjectiveCriteria);
            Object object = (List)this.scoreObjectiveCriterias.get(par2ScoreObjectiveCriteria);

            if (object == null)
            {
                object = new ArrayList();
                this.scoreObjectiveCriterias.put(par2ScoreObjectiveCriteria, object);
            }

            ((List)object).add(scoreobjective);
            this.scoreObjectives.put(par1Str, scoreobjective);
            this.func_96522_a(scoreobjective);
            return scoreobjective;
        }
    }

    public Collection func_96520_a(IScoreObjectiveCriteria par1ScoreObjectiveCriteria)
    {
        Collection collection = (Collection)this.scoreObjectiveCriterias.get(par1ScoreObjectiveCriteria);
        return collection == null ? new ArrayList() : new ArrayList(collection);
    }

    public Score func_96529_a(String par1Str, ScoreObjective par2ScoreObjective)
    {
        Object object = (Map)this.field_96544_c.get(par1Str);

        if (object == null)
        {
            object = new HashMap();
            this.field_96544_c.put(par1Str, object);
        }

        Score score = (Score)((Map)object).get(par2ScoreObjective);

        if (score == null)
        {
            score = new Score(this, par2ScoreObjective, par1Str);
            ((Map)object).put(par2ScoreObjective, score);
        }

        return score;
    }

    public Collection func_96534_i(ScoreObjective par1ScoreObjective)
    {
        ArrayList arraylist = new ArrayList();
        Iterator iterator = this.field_96544_c.values().iterator();

        while (iterator.hasNext())
        {
            Map map = (Map)iterator.next();
            Score score = (Score)map.get(par1ScoreObjective);

            if (score != null)
            {
                arraylist.add(score);
            }
        }

        Collections.sort(arraylist, Score.field_96658_a);
        return arraylist;
    }

    public Collection getScoreObjectives()
    {
        return this.scoreObjectives.values();
    }

    public Collection getObjectiveNames()
    {
        return this.field_96544_c.keySet();
    }

    public void func_96515_c(String par1Str)
    {
        Map map = (Map)this.field_96544_c.remove(par1Str);

        if (map != null)
        {
            this.func_96516_a(par1Str);
        }
    }

    public Collection func_96528_e()
    {
        Collection collection = this.field_96544_c.values();
        ArrayList arraylist = new ArrayList();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext())
        {
            Map map = (Map)iterator.next();
            arraylist.addAll(map.values());
        }

        return arraylist;
    }

    public Map func_96510_d(String par1Str)
    {
        Object object = (Map)this.field_96544_c.get(par1Str);

        if (object == null)
        {
            object = new HashMap();
        }

        return (Map)object;
    }

    public void func_96519_k(ScoreObjective par1ScoreObjective)
    {
        this.scoreObjectives.remove(par1ScoreObjective.getName());

        for (int i = 0; i < 3; ++i)
        {
            if (this.func_96539_a(i) == par1ScoreObjective)
            {
                this.func_96530_a(i, (ScoreObjective)null);
            }
        }

        List list = (List)this.scoreObjectiveCriterias.get(par1ScoreObjective.getCriteria());

        if (list != null)
        {
            list.remove(par1ScoreObjective);
        }

        Iterator iterator = this.field_96544_c.values().iterator();

        while (iterator.hasNext())
        {
            Map map = (Map)iterator.next();
            map.remove(par1ScoreObjective);
        }

        this.func_96533_c(par1ScoreObjective);
    }

    public void func_96530_a(int par1, ScoreObjective par2ScoreObjective)
    {
        this.field_96541_d[par1] = par2ScoreObjective;
    }

    public ScoreObjective func_96539_a(int par1)
    {
        return this.field_96541_d[par1];
    }

    /**
     * Retrieve the ScorePlayerTeam instance identified by the passed team name
     */
    public ScorePlayerTeam getTeam(String par1Str)
    {
        return (ScorePlayerTeam)this.teams.get(par1Str);
    }

    /**
     * Verifies that the given name doesn't already refer to an existing team, creates it otherwise and broadcasts the
     * addition to all players
     */
    public ScorePlayerTeam createTeam(String par1Str)
    {
        ScorePlayerTeam scoreplayerteam = this.getTeam(par1Str);

        if (scoreplayerteam != null)
        {
            throw new IllegalArgumentException("A team with the name \'" + par1Str + "\' already exists!");
        }
        else
        {
            scoreplayerteam = new ScorePlayerTeam(this, par1Str);
            this.teams.put(par1Str, scoreplayerteam);
            this.broadcastTeamCreated(scoreplayerteam);
            return scoreplayerteam;
        }
    }

    /**
     * Removes the team from the scoreboard, updates all player memberships and broadcasts the deletion to all players
     */
    public void removeTeam(ScorePlayerTeam par1ScorePlayerTeam)
    {
        this.teams.remove(par1ScorePlayerTeam.getRegisteredName());
        Iterator iterator = par1ScorePlayerTeam.getMembershipCollection().iterator();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();
            this.teamMemberships.remove(s);
        }

        this.func_96513_c(par1ScorePlayerTeam);
    }

    public boolean func_151392_a(String p_151392_1_, String p_151392_2_)
    {
        if (!this.teams.containsKey(p_151392_2_))
        {
            return false;
        }
        else
        {
            ScorePlayerTeam scoreplayerteam = this.getTeam(p_151392_2_);

            if (this.getPlayersTeam(p_151392_1_) != null)
            {
                this.removePlayerFromTeams(p_151392_1_);
            }

            this.teamMemberships.put(p_151392_1_, scoreplayerteam);
            scoreplayerteam.getMembershipCollection().add(p_151392_1_);
            return true;
        }
    }

    public boolean removePlayerFromTeams(String par1Str)
    {
        ScorePlayerTeam scoreplayerteam = this.getPlayersTeam(par1Str);

        if (scoreplayerteam != null)
        {
            this.removePlayerFromTeam(par1Str, scoreplayerteam);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Removes the given username from the given ScorePlayerTeam. If the player is not on the team then an
     * IllegalStateException is thrown.
     */
    public void removePlayerFromTeam(String par1Str, ScorePlayerTeam par2ScorePlayerTeam)
    {
        if (this.getPlayersTeam(par1Str) != par2ScorePlayerTeam)
        {
            throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team \'" + par2ScorePlayerTeam.getRegisteredName() + "\'.");
        }
        else
        {
            this.teamMemberships.remove(par1Str);
            par2ScorePlayerTeam.getMembershipCollection().remove(par1Str);
        }
    }

    /**
     * Retrieve all registered ScorePlayerTeam names
     */
    public Collection getTeamNames()
    {
        return this.teams.keySet();
    }

    /**
     * Retrieve all registered ScorePlayerTeam instances
     */
    public Collection getTeams()
    {
        return this.teams.values();
    }

    /**
     * Gets the ScorePlayerTeam object for the given username.
     */
    public ScorePlayerTeam getPlayersTeam(String par1Str)
    {
        return (ScorePlayerTeam)this.teamMemberships.get(par1Str);
    }

    public void func_96522_a(ScoreObjective par1ScoreObjective) {}

    public void func_96532_b(ScoreObjective par1ScoreObjective) {}

    public void func_96533_c(ScoreObjective par1ScoreObjective) {}

    public void func_96536_a(Score par1Score) {}

    public void func_96516_a(String par1Str) {}

    /**
     * This packet will notify the players that this team is created, and that will register it on the client
     */
    public void broadcastTeamCreated(ScorePlayerTeam par1ScorePlayerTeam) {}

    /**
     * This packet will notify the players that this team is removed
     */
    public void broadcastTeamRemoved(ScorePlayerTeam par1ScorePlayerTeam) {}

    public void func_96513_c(ScorePlayerTeam par1ScorePlayerTeam) {}

    /**
     * Returns 'list' for 0, 'sidebar' for 1, 'belowName for 2, otherwise null.
     */
    public static String getObjectiveDisplaySlot(int par0)
    {
        switch (par0)
        {
            case 0:
                return "list";
            case 1:
                return "sidebar";
            case 2:
                return "belowName";
            default:
                return null;
        }
    }

    /**
     * Returns 0 for (case-insensitive) 'list', 1 for 'sidebar', 2 for 'belowName', otherwise -1.
     */
    public static int getObjectiveDisplaySlotNumber(String par0Str)
    {
        return par0Str.equalsIgnoreCase("list") ? 0 : (par0Str.equalsIgnoreCase("sidebar") ? 1 : (par0Str.equalsIgnoreCase("belowName") ? 2 : -1));
    }
}