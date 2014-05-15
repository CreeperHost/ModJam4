package net.minecraft.entity;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fluids.IFluidBlock;

public abstract class Entity
{
    private static int nextEntityID;
    private int entityId;
    public double renderDistanceWeight;
    /**
     * Blocks entities from spawning when they do their AABB check to make sure the spot is clear of entities that can
     * prevent spawning.
     */
    public boolean preventEntitySpawning;
    /** The entity that is riding this entity */
    public Entity riddenByEntity;
    /** The entity we are currently riding */
    public Entity ridingEntity;
    public boolean forceSpawn;
    /** Reference to the World object. */
    public World worldObj;
    public double prevPosX;
    public double prevPosY;
    public double prevPosZ;
    /** Entity position X */
    public double posX;
    /** Entity position Y */
    public double posY;
    /** Entity position Z */
    public double posZ;
    /** Entity motion X */
    public double motionX;
    /** Entity motion Y */
    public double motionY;
    /** Entity motion Z */
    public double motionZ;
    /** Entity rotation Yaw */
    public float rotationYaw;
    /** Entity rotation Pitch */
    public float rotationPitch;
    public float prevRotationYaw;
    public float prevRotationPitch;
    /** Axis aligned bounding box. */
    public final AxisAlignedBB boundingBox;
    public boolean onGround;
    /**
     * True if after a move this entity has collided with something on X- or Z-axis
     */
    public boolean isCollidedHorizontally;
    /**
     * True if after a move this entity has collided with something on Y-axis
     */
    public boolean isCollidedVertically;
    /**
     * True if after a move this entity has collided with something either vertically or horizontally
     */
    public boolean isCollided;
    public boolean velocityChanged;
    protected boolean isInWeb;
    public boolean field_70135_K;
    /**
     * Gets set by setDead, so this must be the flag whether an Entity is dead (inactive may be better term)
     */
    public boolean isDead;
    public float yOffset;
    /** How wide this entity is considered to be */
    public float width;
    /** How high this entity is considered to be */
    public float height;
    /** The previous ticks distance walked multiplied by 0.6 */
    public float prevDistanceWalkedModified;
    /** The distance walked multiplied by 0.6 */
    public float distanceWalkedModified;
    public float distanceWalkedOnStepModified;
    public float fallDistance;
    /**
     * The distance that has to be exceeded in order to triger a new step sound and an onEntityWalking event on a block
     */
    private int nextStepDistance;
    /**
     * The entity's X coordinate at the previous tick, used to calculate position during rendering routines
     */
    public double lastTickPosX;
    /**
     * The entity's Y coordinate at the previous tick, used to calculate position during rendering routines
     */
    public double lastTickPosY;
    /**
     * The entity's Z coordinate at the previous tick, used to calculate position during rendering routines
     */
    public double lastTickPosZ;
    public float ySize;
    /**
     * How high this entity can step up when running into a block to try to get over it (currently make note the entity
     * will always step up this amount and not just the amount needed)
     */
    public float stepHeight;
    /**
     * Whether this entity won't clip with collision or not (make note it won't disable gravity)
     */
    public boolean noClip;
    /**
     * Reduces the velocity applied by entity collisions by the specified percent.
     */
    public float entityCollisionReduction;
    protected Random rand;
    /** How many ticks has this entity had ran since being alive */
    public int ticksExisted;
    /**
     * The amount of ticks you have to stand inside of fire before be set on fire
     */
    public int fireResistance;
    private int fire;
    /**
     * Whether this entity is currently inside of water (if it handles water movement that is)
     */
    protected boolean inWater;
    /**
     * Remaining time an entity will be "immune" to further damage after being hurt.
     */
    public int hurtResistantTime;
    private boolean firstUpdate;
    protected boolean isImmuneToFire;
    protected DataWatcher dataWatcher;
    private double entityRiderPitchDelta;
    private double entityRiderYawDelta;
    /** Has this entity been added to the chunk its within */
    public boolean addedToChunk;
    public int chunkCoordX;
    public int chunkCoordY;
    public int chunkCoordZ;
    @SideOnly(Side.CLIENT)
    public int serverPosX;
    @SideOnly(Side.CLIENT)
    public int serverPosY;
    @SideOnly(Side.CLIENT)
    public int serverPosZ;
    /**
     * Render entity even if it is outside the camera frustum. Only true in EntityFish for now. Used in RenderGlobal:
     * render if ignoreFrustumCheck or in frustum.
     */
    public boolean ignoreFrustumCheck;
    public boolean isAirBorne;
    public int timeUntilPortal;
    /** Whether the entity is inside a Portal */
    protected boolean inPortal;
    protected int portalCounter;
    /** Which dimension the player is in (-1 = the Nether, 0 = normal world) */
    public int dimension;
    protected int teleportDirection;
    private boolean invulnerable;
    protected UUID entityUniqueID;
    public Entity.EnumEntitySize myEntitySize;
    private static final String __OBFID = "CL_00001533";
    /** Forge: Used to store custom data for each entity. */
    private NBTTagCompound customEntityData;
    public boolean captureDrops = false;
    public ArrayList<EntityItem> capturedDrops = new ArrayList<EntityItem>();
    private UUID persistentID;

    private HashMap<String, IExtendedEntityProperties> extendedProperties;

    public int getEntityId()
    {
        return this.entityId;
    }

    public void setEntityId(int p_145769_1_)
    {
        this.entityId = p_145769_1_;
    }

    public Entity(World par1World)
    {
        this.entityId = nextEntityID++;
        this.renderDistanceWeight = 1.0D;
        this.boundingBox = AxisAlignedBB.getBoundingBox(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        this.field_70135_K = true;
        this.width = 0.6F;
        this.height = 1.8F;
        this.nextStepDistance = 1;
        this.rand = new Random();
        this.fireResistance = 1;
        this.firstUpdate = true;
        this.entityUniqueID = UUID.randomUUID();
        this.myEntitySize = Entity.EnumEntitySize.SIZE_2;
        this.worldObj = par1World;
        this.setPosition(0.0D, 0.0D, 0.0D);

        if (par1World != null)
        {
            this.dimension = par1World.provider.dimensionId;
        }

        this.dataWatcher = new DataWatcher(this);
        this.dataWatcher.addObject(0, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(1, Short.valueOf((short)300));
        this.entityInit();

        extendedProperties = new HashMap<String, IExtendedEntityProperties>();

        MinecraftForge.EVENT_BUS.post(new EntityEvent.EntityConstructing(this));

        for (IExtendedEntityProperties props : this.extendedProperties.values())
        {
            props.init(this, par1World);
        }
    }

    protected abstract void entityInit();

    public DataWatcher getDataWatcher()
    {
        return this.dataWatcher;
    }

    public boolean equals(Object par1Obj)
    {
        return par1Obj instanceof Entity ? ((Entity)par1Obj).entityId == this.entityId : false;
    }

    public int hashCode()
    {
        return this.entityId;
    }

    /**
     * Keeps moving the entity up so it isn't colliding with blocks and other requirements for this entity to be spawned
     * (only actually used on players though its also on Entity)
     */
    @SideOnly(Side.CLIENT)
    protected void preparePlayerToSpawn()
    {
        if (this.worldObj != null)
        {
            while (this.posY > 0.0D)
            {
                this.setPosition(this.posX, this.posY, this.posZ);

                if (this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty())
                {
                    break;
                }

                ++this.posY;
            }

            this.motionX = this.motionY = this.motionZ = 0.0D;
            this.rotationPitch = 0.0F;
        }
    }

    /**
     * Will get destroyed next tick.
     */
    public void setDead()
    {
        this.isDead = true;
    }

    /**
     * Sets the width and height of the entity. Args: width, height
     */
    protected void setSize(float par1, float par2)
    {
        float f2;

        if (par1 != this.width || par2 != this.height)
        {
            f2 = this.width;
            this.width = par1;
            this.height = par2;
            this.boundingBox.maxX = this.boundingBox.minX + (double)this.width;
            this.boundingBox.maxZ = this.boundingBox.minZ + (double)this.width;
            this.boundingBox.maxY = this.boundingBox.minY + (double)this.height;

            if (this.width > f2 && !this.firstUpdate && !this.worldObj.isRemote)
            {
                this.moveEntity((double)(f2 - this.width), 0.0D, (double)(f2 - this.width));
            }
        }

        f2 = par1 % 2.0F;

        if ((double)f2 < 0.375D)
        {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_1;
        }
        else if ((double)f2 < 0.75D)
        {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_2;
        }
        else if ((double)f2 < 1.0D)
        {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_3;
        }
        else if ((double)f2 < 1.375D)
        {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_4;
        }
        else if ((double)f2 < 1.75D)
        {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_5;
        }
        else
        {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_6;
        }
    }

    /**
     * Sets the rotation of the entity
     */
    protected void setRotation(float par1, float par2)
    {
        this.rotationYaw = par1 % 360.0F;
        this.rotationPitch = par2 % 360.0F;
    }

    /**
     * Sets the x,y,z of the entity from the given parameters. Also seems to set up a bounding box.
     */
    public void setPosition(double par1, double par3, double par5)
    {
        this.posX = par1;
        this.posY = par3;
        this.posZ = par5;
        float f = this.width / 2.0F;
        float f1 = this.height;
        this.boundingBox.setBounds(par1 - (double)f, par3 - (double)this.yOffset + (double)this.ySize, par5 - (double)f, par1 + (double)f, par3 - (double)this.yOffset + (double)this.ySize + (double)f1, par5 + (double)f);
    }

    /**
     * Adds par1*0.15 to the entity's yaw, and *subtracts* par2*0.15 from the pitch. Clamps pitch from -90 to 90. Both
     * arguments in degrees.
     */
    @SideOnly(Side.CLIENT)
    public void setAngles(float par1, float par2)
    {
        float f2 = this.rotationPitch;
        float f3 = this.rotationYaw;
        this.rotationYaw = (float)((double)this.rotationYaw + (double)par1 * 0.15D);
        this.rotationPitch = (float)((double)this.rotationPitch - (double)par2 * 0.15D);

        if (this.rotationPitch < -90.0F)
        {
            this.rotationPitch = -90.0F;
        }

        if (this.rotationPitch > 90.0F)
        {
            this.rotationPitch = 90.0F;
        }

        this.prevRotationPitch += this.rotationPitch - f2;
        this.prevRotationYaw += this.rotationYaw - f3;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.onEntityUpdate();
    }

    /**
     * Gets called every tick from main Entity class
     */
    public void onEntityUpdate()
    {
        this.worldObj.theProfiler.startSection("entityBaseTick");

        if (this.ridingEntity != null && this.ridingEntity.isDead)
        {
            this.ridingEntity = null;
        }

        this.prevDistanceWalkedModified = this.distanceWalkedModified;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;
        int i;

        if (!this.worldObj.isRemote && this.worldObj instanceof WorldServer)
        {
            this.worldObj.theProfiler.startSection("portal");
            MinecraftServer minecraftserver = ((WorldServer)this.worldObj).func_73046_m();
            i = this.getMaxInPortalTime();

            if (this.inPortal)
            {
                if (minecraftserver.getAllowNether())
                {
                    if (this.ridingEntity == null && this.portalCounter++ >= i)
                    {
                        this.portalCounter = i;
                        this.timeUntilPortal = this.getPortalCooldown();
                        byte b0;

                        if (this.worldObj.provider.dimensionId == -1)
                        {
                            b0 = 0;
                        }
                        else
                        {
                            b0 = -1;
                        }

                        this.travelToDimension(b0);
                    }

                    this.inPortal = false;
                }
            }
            else
            {
                if (this.portalCounter > 0)
                {
                    this.portalCounter -= 4;
                }

                if (this.portalCounter < 0)
                {
                    this.portalCounter = 0;
                }
            }

            if (this.timeUntilPortal > 0)
            {
                --this.timeUntilPortal;
            }

            this.worldObj.theProfiler.endSection();
        }

        if (this.isSprinting() && !this.isInWater())
        {
            int j = MathHelper.floor_double(this.posX);
            i = MathHelper.floor_double(this.posY - 0.20000000298023224D - (double)this.yOffset);
            int k = MathHelper.floor_double(this.posZ);
            Block block = this.worldObj.getBlock(j, i, k);

            if (block.getMaterial() != Material.air)
            {
                this.worldObj.spawnParticle("blockcrack_" + Block.getIdFromBlock(block) + "_" + this.worldObj.getBlockMetadata(j, i, k), this.posX + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, this.boundingBox.minY + 0.1D, this.posZ + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, -this.motionX * 4.0D, 1.5D, -this.motionZ * 4.0D);
            }
        }

        this.handleWaterMovement();

        if (this.worldObj.isRemote)
        {
            this.fire = 0;
        }
        else if (this.fire > 0)
        {
            if (this.isImmuneToFire)
            {
                this.fire -= 4;

                if (this.fire < 0)
                {
                    this.fire = 0;
                }
            }
            else
            {
                if (this.fire % 20 == 0)
                {
                    this.attackEntityFrom(DamageSource.onFire, 1.0F);
                }

                --this.fire;
            }
        }

        if (this.handleLavaMovement())
        {
            this.setOnFireFromLava();
            this.fallDistance *= 0.5F;
        }

        if (this.posY < -64.0D)
        {
            this.kill();
        }

        if (!this.worldObj.isRemote)
        {
            this.setFlag(0, this.fire > 0);
        }

        this.firstUpdate = false;
        this.worldObj.theProfiler.endSection();
    }

    /**
     * Return the amount of time this entity should stay in a portal before being transported.
     */
    public int getMaxInPortalTime()
    {
        return 0;
    }

    /**
     * Called whenever the entity is walking inside of lava.
     */
    protected void setOnFireFromLava()
    {
        if (!this.isImmuneToFire)
        {
            this.attackEntityFrom(DamageSource.lava, 4.0F);
            this.setFire(15);
        }
    }

    /**
     * Sets entity to burn for x amount of seconds, cannot lower amount of existing fire.
     */
    public void setFire(int par1)
    {
        int j = par1 * 20;
        j = EnchantmentProtection.getFireTimeForEntity(this, j);

        if (this.fire < j)
        {
            this.fire = j;
        }
    }

    /**
     * Removes fire from entity.
     */
    public void extinguish()
    {
        this.fire = 0;
    }

    /**
     * sets the dead flag. Used when you fall off the bottom of the world.
     */
    protected void kill()
    {
        this.setDead();
    }

    /**
     * Checks if the offset position from the entity's current position is inside of liquid. Args: x, y, z
     */
    public boolean isOffsetPositionInLiquid(double par1, double par3, double par5)
    {
        AxisAlignedBB axisalignedbb = this.boundingBox.getOffsetBoundingBox(par1, par3, par5);
        List list = this.worldObj.getCollidingBoundingBoxes(this, axisalignedbb);
        return !list.isEmpty() ? false : !this.worldObj.isAnyLiquid(axisalignedbb);
    }

    /**
     * Tries to moves the entity by the passed in displacement. Args: x, y, z
     */
    public void moveEntity(double par1, double par3, double par5)
    {
        if (this.noClip)
        {
            this.boundingBox.offset(par1, par3, par5);
            this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
            this.posY = this.boundingBox.minY + (double)this.yOffset - (double)this.ySize;
            this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
        }
        else
        {
            this.worldObj.theProfiler.startSection("move");
            this.ySize *= 0.4F;
            double d3 = this.posX;
            double d4 = this.posY;
            double d5 = this.posZ;

            if (this.isInWeb)
            {
                this.isInWeb = false;
                par1 *= 0.25D;
                par3 *= 0.05000000074505806D;
                par5 *= 0.25D;
                this.motionX = 0.0D;
                this.motionY = 0.0D;
                this.motionZ = 0.0D;
            }

            double d6 = par1;
            double d7 = par3;
            double d8 = par5;
            AxisAlignedBB axisalignedbb = this.boundingBox.copy();
            boolean flag = this.onGround && this.isSneaking() && this instanceof EntityPlayer;

            if (flag)
            {
                double d9;

                for (d9 = 0.05D; par1 != 0.0D && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(par1, -1.0D, 0.0D)).isEmpty(); d6 = par1)
                {
                    if (par1 < d9 && par1 >= -d9)
                    {
                        par1 = 0.0D;
                    }
                    else if (par1 > 0.0D)
                    {
                        par1 -= d9;
                    }
                    else
                    {
                        par1 += d9;
                    }
                }

                for (; par5 != 0.0D && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(0.0D, -1.0D, par5)).isEmpty(); d8 = par5)
                {
                    if (par5 < d9 && par5 >= -d9)
                    {
                        par5 = 0.0D;
                    }
                    else if (par5 > 0.0D)
                    {
                        par5 -= d9;
                    }
                    else
                    {
                        par5 += d9;
                    }
                }

                while (par1 != 0.0D && par5 != 0.0D && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(par1, -1.0D, par5)).isEmpty())
                {
                    if (par1 < d9 && par1 >= -d9)
                    {
                        par1 = 0.0D;
                    }
                    else if (par1 > 0.0D)
                    {
                        par1 -= d9;
                    }
                    else
                    {
                        par1 += d9;
                    }

                    if (par5 < d9 && par5 >= -d9)
                    {
                        par5 = 0.0D;
                    }
                    else if (par5 > 0.0D)
                    {
                        par5 -= d9;
                    }
                    else
                    {
                        par5 += d9;
                    }

                    d6 = par1;
                    d8 = par5;
                }
            }

            List list = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(par1, par3, par5));

            for (int i = 0; i < list.size(); ++i)
            {
                par3 = ((AxisAlignedBB)list.get(i)).calculateYOffset(this.boundingBox, par3);
            }

            this.boundingBox.offset(0.0D, par3, 0.0D);

            if (!this.field_70135_K && d7 != par3)
            {
                par5 = 0.0D;
                par3 = 0.0D;
                par1 = 0.0D;
            }

            boolean flag1 = this.onGround || d7 != par3 && d7 < 0.0D;
            int j;

            for (j = 0; j < list.size(); ++j)
            {
                par1 = ((AxisAlignedBB)list.get(j)).calculateXOffset(this.boundingBox, par1);
            }

            this.boundingBox.offset(par1, 0.0D, 0.0D);

            if (!this.field_70135_K && d6 != par1)
            {
                par5 = 0.0D;
                par3 = 0.0D;
                par1 = 0.0D;
            }

            for (j = 0; j < list.size(); ++j)
            {
                par5 = ((AxisAlignedBB)list.get(j)).calculateZOffset(this.boundingBox, par5);
            }

            this.boundingBox.offset(0.0D, 0.0D, par5);

            if (!this.field_70135_K && d8 != par5)
            {
                par5 = 0.0D;
                par3 = 0.0D;
                par1 = 0.0D;
            }

            double d10;
            double d11;
            int k;
            double d12;

            if (this.stepHeight > 0.0F && flag1 && (flag || this.ySize < 0.05F) && (d6 != par1 || d8 != par5))
            {
                d12 = par1;
                d10 = par3;
                d11 = par5;
                par1 = d6;
                par3 = (double)this.stepHeight;
                par5 = d8;
                AxisAlignedBB axisalignedbb1 = this.boundingBox.copy();
                this.boundingBox.setBB(axisalignedbb);
                list = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(d6, par3, d8));

                for (k = 0; k < list.size(); ++k)
                {
                    par3 = ((AxisAlignedBB)list.get(k)).calculateYOffset(this.boundingBox, par3);
                }

                this.boundingBox.offset(0.0D, par3, 0.0D);

                if (!this.field_70135_K && d7 != par3)
                {
                    par5 = 0.0D;
                    par3 = 0.0D;
                    par1 = 0.0D;
                }

                for (k = 0; k < list.size(); ++k)
                {
                    par1 = ((AxisAlignedBB)list.get(k)).calculateXOffset(this.boundingBox, par1);
                }

                this.boundingBox.offset(par1, 0.0D, 0.0D);

                if (!this.field_70135_K && d6 != par1)
                {
                    par5 = 0.0D;
                    par3 = 0.0D;
                    par1 = 0.0D;
                }

                for (k = 0; k < list.size(); ++k)
                {
                    par5 = ((AxisAlignedBB)list.get(k)).calculateZOffset(this.boundingBox, par5);
                }

                this.boundingBox.offset(0.0D, 0.0D, par5);

                if (!this.field_70135_K && d8 != par5)
                {
                    par5 = 0.0D;
                    par3 = 0.0D;
                    par1 = 0.0D;
                }

                if (!this.field_70135_K && d7 != par3)
                {
                    par5 = 0.0D;
                    par3 = 0.0D;
                    par1 = 0.0D;
                }
                else
                {
                    par3 = (double)(-this.stepHeight);

                    for (k = 0; k < list.size(); ++k)
                    {
                        par3 = ((AxisAlignedBB)list.get(k)).calculateYOffset(this.boundingBox, par3);
                    }

                    this.boundingBox.offset(0.0D, par3, 0.0D);
                }

                if (d12 * d12 + d11 * d11 >= par1 * par1 + par5 * par5)
                {
                    par1 = d12;
                    par3 = d10;
                    par5 = d11;
                    this.boundingBox.setBB(axisalignedbb1);
                }
            }

            this.worldObj.theProfiler.endSection();
            this.worldObj.theProfiler.startSection("rest");
            this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
            this.posY = this.boundingBox.minY + (double)this.yOffset - (double)this.ySize;
            this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
            this.isCollidedHorizontally = d6 != par1 || d8 != par5;
            this.isCollidedVertically = d7 != par3;
            this.onGround = d7 != par3 && d7 < 0.0D;
            this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
            this.updateFallState(par3, this.onGround);

            if (d6 != par1)
            {
                this.motionX = 0.0D;
            }

            if (d7 != par3)
            {
                this.motionY = 0.0D;
            }

            if (d8 != par5)
            {
                this.motionZ = 0.0D;
            }

            d12 = this.posX - d3;
            d10 = this.posY - d4;
            d11 = this.posZ - d5;

            if (this.canTriggerWalking() && !flag && this.ridingEntity == null)
            {
                int j1 = MathHelper.floor_double(this.posX);
                k = MathHelper.floor_double(this.posY - 0.20000000298023224D - (double)this.yOffset);
                int l = MathHelper.floor_double(this.posZ);
                Block block = this.worldObj.getBlock(j1, k, l);
                int i1 = this.worldObj.getBlock(j1, k - 1, l).getRenderType();

                if (i1 == 11 || i1 == 32 || i1 == 21)
                {
                    block = this.worldObj.getBlock(j1, k - 1, l);
                }

                if (block != Blocks.ladder)
                {
                    d10 = 0.0D;
                }

                this.distanceWalkedModified = (float)((double)this.distanceWalkedModified + (double)MathHelper.sqrt_double(d12 * d12 + d11 * d11) * 0.6D);
                this.distanceWalkedOnStepModified = (float)((double)this.distanceWalkedOnStepModified + (double)MathHelper.sqrt_double(d12 * d12 + d10 * d10 + d11 * d11) * 0.6D);

                if (this.distanceWalkedOnStepModified > (float)this.nextStepDistance && block.getMaterial() != Material.air)
                {
                    this.nextStepDistance = (int)this.distanceWalkedOnStepModified + 1;

                    if (this.isInWater())
                    {
                        float f = MathHelper.sqrt_double(this.motionX * this.motionX * 0.20000000298023224D + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.20000000298023224D) * 0.35F;

                        if (f > 1.0F)
                        {
                            f = 1.0F;
                        }

                        this.playSound(this.getSwimSound(), f, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                    }

                    this.func_145780_a(j1, k, l, block);
                    block.onEntityWalking(this.worldObj, j1, k, l, this);
                }
            }

            try
            {
                this.func_145775_I();
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
                this.addEntityCrashInfo(crashreportcategory);
                throw new ReportedException(crashreport);
            }

            boolean flag2 = this.isWet();

            if (this.worldObj.func_147470_e(this.boundingBox.contract(0.001D, 0.001D, 0.001D)))
            {
                this.dealFireDamage(1);

                if (!flag2)
                {
                    ++this.fire;

                    if (this.fire == 0)
                    {
                        this.setFire(8);
                    }
                }
            }
            else if (this.fire <= 0)
            {
                this.fire = -this.fireResistance;
            }

            if (flag2 && this.fire > 0)
            {
                this.playSound("random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                this.fire = -this.fireResistance;
            }

            this.worldObj.theProfiler.endSection();
        }
    }

    protected String getSwimSound()
    {
        return "game.neutral.swim";
    }

    protected void func_145775_I()
    {
        int i = MathHelper.floor_double(this.boundingBox.minX + 0.001D);
        int j = MathHelper.floor_double(this.boundingBox.minY + 0.001D);
        int k = MathHelper.floor_double(this.boundingBox.minZ + 0.001D);
        int l = MathHelper.floor_double(this.boundingBox.maxX - 0.001D);
        int i1 = MathHelper.floor_double(this.boundingBox.maxY - 0.001D);
        int j1 = MathHelper.floor_double(this.boundingBox.maxZ - 0.001D);

        if (this.worldObj.checkChunksExist(i, j, k, l, i1, j1))
        {
            for (int k1 = i; k1 <= l; ++k1)
            {
                for (int l1 = j; l1 <= i1; ++l1)
                {
                    for (int i2 = k; i2 <= j1; ++i2)
                    {
                        Block block = this.worldObj.getBlock(k1, l1, i2);

                        try
                        {
                            block.onEntityCollidedWithBlock(this.worldObj, k1, l1, i2, this);
                        }
                        catch (Throwable throwable)
                        {
                            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Colliding entity with block");
                            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being collided with");
                            CrashReportCategory.func_147153_a(crashreportcategory, k1, l1, i2, block, this.worldObj.getBlockMetadata(k1, l1, i2));
                            throw new ReportedException(crashreport);
                        }
                    }
                }
            }
        }
    }

    protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_)
    {
        Block.SoundType soundtype = p_145780_4_.stepSound;

        if (this.worldObj.getBlock(p_145780_1_, p_145780_2_ + 1, p_145780_3_) == Blocks.snow_layer)
        {
            soundtype = Blocks.snow_layer.stepSound;
            this.playSound(soundtype.getStepResourcePath(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
        }
        else if (!p_145780_4_.getMaterial().isLiquid())
        {
            this.playSound(soundtype.getStepResourcePath(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
        }
    }

    public void playSound(String par1Str, float par2, float par3)
    {
        this.worldObj.playSoundAtEntity(this, par1Str, par2, par3);
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return true;
    }

    /**
     * Takes in the distance the entity has fallen this tick and whether its on the ground to update the fall distance
     * and deal fall damage if landing on the ground.  Args: distanceFallenThisTick, onGround
     */
    protected void updateFallState(double par1, boolean par3)
    {
        if (par3)
        {
            if (this.fallDistance > 0.0F)
            {
                this.fall(this.fallDistance);
                this.fallDistance = 0.0F;
            }
        }
        else if (par1 < 0.0D)
        {
            this.fallDistance = (float)((double)this.fallDistance - par1);
        }
    }

    /**
     * returns the bounding box for this entity
     */
    public AxisAlignedBB getBoundingBox()
    {
        return null;
    }

    /**
     * Will deal the specified amount of damage to the entity if the entity isn't immune to fire damage. Args:
     * amountDamage
     */
    protected void dealFireDamage(int par1)
    {
        if (!this.isImmuneToFire)
        {
            this.attackEntityFrom(DamageSource.inFire, (float)par1);
        }
    }

    public final boolean isImmuneToFire()
    {
        return this.isImmuneToFire;
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(float par1)
    {
        if (this.riddenByEntity != null)
        {
            this.riddenByEntity.fall(par1);
        }
    }

    /**
     * Checks if this entity is either in water or on an open air block in rain (used in wolves).
     */
    public boolean isWet()
    {
        return this.inWater || this.worldObj.canLightningStrikeAt(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) || this.worldObj.canLightningStrikeAt(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + (double)this.height), MathHelper.floor_double(this.posZ));
    }

    /**
     * Checks if this entity is inside water (if inWater field is true as a result of handleWaterMovement() returning
     * true)
     */
    public boolean isInWater()
    {
        return this.inWater;
    }

    /**
     * Returns if this entity is in water and will end up adding the waters velocity to the entity
     */
    public boolean handleWaterMovement()
    {
        if (this.worldObj.handleMaterialAcceleration(this.boundingBox.expand(0.0D, -0.4000000059604645D, 0.0D).contract(0.001D, 0.001D, 0.001D), Material.water, this))
        {
            if (!this.inWater && !this.firstUpdate)
            {
                float f = MathHelper.sqrt_double(this.motionX * this.motionX * 0.20000000298023224D + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.20000000298023224D) * 0.2F;

                if (f > 1.0F)
                {
                    f = 1.0F;
                }

                this.playSound(this.getSplashSound(), f, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                float f1 = (float)MathHelper.floor_double(this.boundingBox.minY);
                int i;
                float f2;
                float f3;

                for (i = 0; (float)i < 1.0F + this.width * 20.0F; ++i)
                {
                    f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    f3 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    this.worldObj.spawnParticle("bubble", this.posX + (double)f2, (double)(f1 + 1.0F), this.posZ + (double)f3, this.motionX, this.motionY - (double)(this.rand.nextFloat() * 0.2F), this.motionZ);
                }

                for (i = 0; (float)i < 1.0F + this.width * 20.0F; ++i)
                {
                    f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    f3 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    this.worldObj.spawnParticle("splash", this.posX + (double)f2, (double)(f1 + 1.0F), this.posZ + (double)f3, this.motionX, this.motionY, this.motionZ);
                }
            }

            this.fallDistance = 0.0F;
            this.inWater = true;
            this.fire = 0;
        }
        else
        {
            this.inWater = false;
        }

        return this.inWater;
    }

    protected String getSplashSound()
    {
        return "game.neutral.swim.splash";
    }

    /**
     * Checks if the current block the entity is within of the specified material type
     */
    public boolean isInsideOfMaterial(Material par1Material)
    {
        double d0 = this.posY + (double)this.getEyeHeight();
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_float((float)MathHelper.floor_double(d0));
        int k = MathHelper.floor_double(this.posZ);
        Block block = this.worldObj.getBlock(i, j, k);

        if (block.getMaterial() == par1Material)
        {
            double filled = 1.0f; //If it's not a liquid assume it's a solid block
            if (block instanceof IFluidBlock)
            {
                filled = ((IFluidBlock)block).getFilledPercentage(worldObj, i, j, k);
            }

            if (filled < 0)
            {
                filled *= -1;
                //filled -= 0.11111111F; //Why this is needed.. not sure...
                return d0 > (double)(j + (1 - filled));
            }
            else
            {
                return d0 < (double)(j + filled);
            }
        }
        else
        {
            return false;
        }
    }

    public float getEyeHeight()
    {
        return 0.0F;
    }

    /**
     * Whether or not the current entity is in lava
     */
    public boolean handleLavaMovement()
    {
        return this.worldObj.isMaterialInBB(this.boundingBox.expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), Material.lava);
    }

    /**
     * Used in both water and by flying objects
     */
    public void moveFlying(float par1, float par2, float par3)
    {
        float f3 = par1 * par1 + par2 * par2;

        if (f3 >= 1.0E-4F)
        {
            f3 = MathHelper.sqrt_float(f3);

            if (f3 < 1.0F)
            {
                f3 = 1.0F;
            }

            f3 = par3 / f3;
            par1 *= f3;
            par2 *= f3;
            float f4 = MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F);
            float f5 = MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F);
            this.motionX += (double)(par1 * f5 - par2 * f4);
            this.motionZ += (double)(par2 * f5 + par1 * f4);
        }
    }

    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float par1)
    {
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.posZ);

        if (this.worldObj.blockExists(i, 0, j))
        {
            double d0 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66D;
            int k = MathHelper.floor_double(this.posY - (double)this.yOffset + d0);
            return this.worldObj.getLightBrightnessForSkyBlocks(i, k, j, 0);
        }
        else
        {
            return 0;
        }
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness(float par1)
    {
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.posZ);

        if (this.worldObj.blockExists(i, 0, j))
        {
            double d0 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66D;
            int k = MathHelper.floor_double(this.posY - (double)this.yOffset + d0);
            return this.worldObj.getLightBrightness(i, k, j);
        }
        else
        {
            return 0.0F;
        }
    }

    /**
     * Sets the reference to the World object.
     */
    public void setWorld(World par1World)
    {
        this.worldObj = par1World;
    }

    /**
     * Sets the entity's position and rotation. Args: posX, posY, posZ, yaw, pitch
     */
    public void setPositionAndRotation(double par1, double par3, double par5, float par7, float par8)
    {
        this.prevPosX = this.posX = par1;
        this.prevPosY = this.posY = par3;
        this.prevPosZ = this.posZ = par5;
        this.prevRotationYaw = this.rotationYaw = par7;
        this.prevRotationPitch = this.rotationPitch = par8;
        this.ySize = 0.0F;
        double d3 = (double)(this.prevRotationYaw - par7);

        if (d3 < -180.0D)
        {
            this.prevRotationYaw += 360.0F;
        }

        if (d3 >= 180.0D)
        {
            this.prevRotationYaw -= 360.0F;
        }

        this.setPosition(this.posX, this.posY, this.posZ);
        this.setRotation(par7, par8);
    }

    /**
     * Sets the location and Yaw/Pitch of an entity in the world
     */
    public void setLocationAndAngles(double par1, double par3, double par5, float par7, float par8)
    {
        this.lastTickPosX = this.prevPosX = this.posX = par1;
        this.lastTickPosY = this.prevPosY = this.posY = par3 + (double)this.yOffset;
        this.lastTickPosZ = this.prevPosZ = this.posZ = par5;
        this.rotationYaw = par7;
        this.rotationPitch = par8;
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    /**
     * Returns the distance to the entity. Args: entity
     */
    public float getDistanceToEntity(Entity par1Entity)
    {
        float f = (float)(this.posX - par1Entity.posX);
        float f1 = (float)(this.posY - par1Entity.posY);
        float f2 = (float)(this.posZ - par1Entity.posZ);
        return MathHelper.sqrt_float(f * f + f1 * f1 + f2 * f2);
    }

    /**
     * Gets the squared distance to the position. Args: x, y, z
     */
    public double getDistanceSq(double par1, double par3, double par5)
    {
        double d3 = this.posX - par1;
        double d4 = this.posY - par3;
        double d5 = this.posZ - par5;
        return d3 * d3 + d4 * d4 + d5 * d5;
    }

    /**
     * Gets the distance to the position. Args: x, y, z
     */
    public double getDistance(double par1, double par3, double par5)
    {
        double d3 = this.posX - par1;
        double d4 = this.posY - par3;
        double d5 = this.posZ - par5;
        return (double)MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
    }

    /**
     * Returns the squared distance to the entity. Args: entity
     */
    public double getDistanceSqToEntity(Entity par1Entity)
    {
        double d0 = this.posX - par1Entity.posX;
        double d1 = this.posY - par1Entity.posY;
        double d2 = this.posZ - par1Entity.posZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {}

    /**
     * Applies a velocity to each of the entities pushing them away from each other. Args: entity
     */
    public void applyEntityCollision(Entity par1Entity)
    {
        if (par1Entity.riddenByEntity != this && par1Entity.ridingEntity != this)
        {
            double d0 = par1Entity.posX - this.posX;
            double d1 = par1Entity.posZ - this.posZ;
            double d2 = MathHelper.abs_max(d0, d1);

            if (d2 >= 0.009999999776482582D)
            {
                d2 = (double)MathHelper.sqrt_double(d2);
                d0 /= d2;
                d1 /= d2;
                double d3 = 1.0D / d2;

                if (d3 > 1.0D)
                {
                    d3 = 1.0D;
                }

                d0 *= d3;
                d1 *= d3;
                d0 *= 0.05000000074505806D;
                d1 *= 0.05000000074505806D;
                d0 *= (double)(1.0F - this.entityCollisionReduction);
                d1 *= (double)(1.0F - this.entityCollisionReduction);
                this.addVelocity(-d0, 0.0D, -d1);
                par1Entity.addVelocity(d0, 0.0D, d1);
            }
        }
    }

    /**
     * Adds to the current velocity of the entity. Args: x, y, z
     */
    public void addVelocity(double par1, double par3, double par5)
    {
        this.motionX += par1;
        this.motionY += par3;
        this.motionZ += par5;
        this.isAirBorne = true;
    }

    /**
     * Sets that this entity has been attacked.
     */
    protected void setBeenAttacked()
    {
        this.velocityChanged = true;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else
        {
            this.setBeenAttacked();
            return false;
        }
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return false;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed()
    {
        return false;
    }

    /**
     * Adds a value to the player score. Currently not actually used and the entity passed in does nothing. Args:
     * entity, scoreToAdd
     */
    public void addToPlayerScore(Entity par1Entity, int par2) {}

    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRender3d(double p_145770_1_, double p_145770_3_, double p_145770_5_)
    {
        double d3 = this.posX - p_145770_1_;
        double d4 = this.posY - p_145770_3_;
        double d5 = this.posZ - p_145770_5_;
        double d6 = d3 * d3 + d4 * d4 + d5 * d5;
        return this.isInRangeToRenderDist(d6);
    }

    /**
     * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
     * length * 64 * renderDistanceWeight Args: distance
     */
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double par1)
    {
        double d1 = this.boundingBox.getAverageEdgeLength();
        d1 *= 64.0D * this.renderDistanceWeight;
        return par1 < d1 * d1;
    }

    /**
     * Like writeToNBTOptional but does not check if the entity is ridden. Used for saving ridden entities with their
     * riders.
     */
    public boolean writeMountToNBT(NBTTagCompound par1NBTTagCompound)
    {
        String s = this.getEntityString();

        if (!this.isDead && s != null)
        {
            par1NBTTagCompound.setString("id", s);
            this.writeToNBT(par1NBTTagCompound);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Either write this entity to the NBT tag given and return true, or return false without doing anything. If this
     * returns false the entity is not saved on disk. Ridden entities return false here as they are saved with their
     * rider.
     */
    public boolean writeToNBTOptional(NBTTagCompound par1NBTTagCompound)
    {
        String s = this.getEntityString();

        if (!this.isDead && s != null && this.riddenByEntity == null)
        {
            par1NBTTagCompound.setString("id", s);
            this.writeToNBT(par1NBTTagCompound);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Save the entity to NBT (calls an abstract helper method to write extra data)
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        try
        {
            par1NBTTagCompound.setTag("Pos", this.newDoubleNBTList(new double[] {this.posX, this.posY + (double)this.ySize, this.posZ}));
            par1NBTTagCompound.setTag("Motion", this.newDoubleNBTList(new double[] {this.motionX, this.motionY, this.motionZ}));
            par1NBTTagCompound.setTag("Rotation", this.newFloatNBTList(new float[] {this.rotationYaw, this.rotationPitch}));
            par1NBTTagCompound.setFloat("FallDistance", this.fallDistance);
            par1NBTTagCompound.setShort("Fire", (short)this.fire);
            par1NBTTagCompound.setShort("Air", (short)this.getAir());
            par1NBTTagCompound.setBoolean("OnGround", this.onGround);
            par1NBTTagCompound.setInteger("Dimension", this.dimension);
            par1NBTTagCompound.setBoolean("Invulnerable", this.invulnerable);
            par1NBTTagCompound.setInteger("PortalCooldown", this.timeUntilPortal);
            par1NBTTagCompound.setLong("UUIDMost", this.getUniqueID().getMostSignificantBits());
            par1NBTTagCompound.setLong("UUIDLeast", this.getUniqueID().getLeastSignificantBits());
            if (customEntityData != null)
            {
                par1NBTTagCompound.setTag("ForgeData", customEntityData);
            }

           for (String identifier : this.extendedProperties.keySet())
           {
                try
                {
                    IExtendedEntityProperties props = this.extendedProperties.get(identifier);
                    props.saveNBTData(par1NBTTagCompound);
                }
                catch (Throwable t)
                {
                    FMLLog.severe("Failed to save extended properties for %s.  This is a mod issue.", identifier);
                    t.printStackTrace();
                }
            }

           this.writeEntityToNBT(par1NBTTagCompound);

            if (this.ridingEntity != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                if (this.ridingEntity.writeMountToNBT(nbttagcompound1))
                {
                    par1NBTTagCompound.setTag("Riding", nbttagcompound1);
                }
            }
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Saving entity NBT");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being saved");
            this.addEntityCrashInfo(crashreportcategory);
            throw new ReportedException(crashreport);
        }
    }

    /**
     * Reads the entity from NBT (calls an abstract helper method to read specialized data)
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        try
        {
            NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Pos", 6);
            NBTTagList nbttaglist1 = par1NBTTagCompound.getTagList("Motion", 6);
            NBTTagList nbttaglist2 = par1NBTTagCompound.getTagList("Rotation", 5);
            this.motionX = nbttaglist1.func_150309_d(0);
            this.motionY = nbttaglist1.func_150309_d(1);
            this.motionZ = nbttaglist1.func_150309_d(2);

            if (Math.abs(this.motionX) > 10.0D)
            {
                this.motionX = 0.0D;
            }

            if (Math.abs(this.motionY) > 10.0D)
            {
                this.motionY = 0.0D;
            }

            if (Math.abs(this.motionZ) > 10.0D)
            {
                this.motionZ = 0.0D;
            }

            this.prevPosX = this.lastTickPosX = this.posX = nbttaglist.func_150309_d(0);
            this.prevPosY = this.lastTickPosY = this.posY = nbttaglist.func_150309_d(1);
            this.prevPosZ = this.lastTickPosZ = this.posZ = nbttaglist.func_150309_d(2);
            this.prevRotationYaw = this.rotationYaw = nbttaglist2.func_150308_e(0);
            this.prevRotationPitch = this.rotationPitch = nbttaglist2.func_150308_e(1);
            this.fallDistance = par1NBTTagCompound.getFloat("FallDistance");
            this.fire = par1NBTTagCompound.getShort("Fire");
            this.setAir(par1NBTTagCompound.getShort("Air"));
            this.onGround = par1NBTTagCompound.getBoolean("OnGround");
            this.dimension = par1NBTTagCompound.getInteger("Dimension");
            this.invulnerable = par1NBTTagCompound.getBoolean("Invulnerable");
            this.timeUntilPortal = par1NBTTagCompound.getInteger("PortalCooldown");

            if (par1NBTTagCompound.hasKey("UUIDMost", 4) && par1NBTTagCompound.hasKey("UUIDLeast", 4))
            {
                this.entityUniqueID = new UUID(par1NBTTagCompound.getLong("UUIDMost"), par1NBTTagCompound.getLong("UUIDLeast"));
            }

            this.setPosition(this.posX, this.posY, this.posZ);
            this.setRotation(this.rotationYaw, this.rotationPitch);
            if (par1NBTTagCompound.hasKey("ForgeData"))
            {
                customEntityData = par1NBTTagCompound.getCompoundTag("ForgeData");
            }

            for (String identifier : this.extendedProperties.keySet())
            {
                try
                {
                    IExtendedEntityProperties props = this.extendedProperties.get(identifier);
                    props.loadNBTData(par1NBTTagCompound);
                }
                catch (Throwable t)
                {
                    FMLLog.severe("Failed to load extended properties for %s.  This is a mod issue.", identifier);
                    t.printStackTrace();
                }
            }

            //Rawr, legacy code, Vanilla added a UUID, keep this so older maps will convert properly
            if (par1NBTTagCompound.hasKey("PersistentIDMSB") && par1NBTTagCompound.hasKey("PersistentIDLSB"))
            {
                this.entityUniqueID = new UUID(par1NBTTagCompound.getLong("PersistentIDMSB"), par1NBTTagCompound.getLong("PersistentIDLSB"));
            }
            this.readEntityFromNBT(par1NBTTagCompound);

            if (this.shouldSetPosAfterLoading())
            {
                this.setPosition(this.posX, this.posY, this.posZ);
            }
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Loading entity NBT");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being loaded");
            this.addEntityCrashInfo(crashreportcategory);
            throw new ReportedException(crashreport);
        }
    }

    protected boolean shouldSetPosAfterLoading()
    {
        return true;
    }

    /**
     * Returns the string that identifies this Entity's class
     */
    protected final String getEntityString()
    {
        return EntityList.getEntityString(this);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected abstract void readEntityFromNBT(NBTTagCompound var1);

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected abstract void writeEntityToNBT(NBTTagCompound var1);

    public void onChunkLoad() {}

    /**
     * creates a NBT list from the array of doubles passed to this function
     */
    protected NBTTagList newDoubleNBTList(double ... par1ArrayOfDouble)
    {
        NBTTagList nbttaglist = new NBTTagList();
        double[] adouble = par1ArrayOfDouble;
        int i = par1ArrayOfDouble.length;

        for (int j = 0; j < i; ++j)
        {
            double d1 = adouble[j];
            nbttaglist.appendTag(new NBTTagDouble(d1));
        }

        return nbttaglist;
    }

    /**
     * Returns a new NBTTagList filled with the specified floats
     */
    protected NBTTagList newFloatNBTList(float ... par1ArrayOfFloat)
    {
        NBTTagList nbttaglist = new NBTTagList();
        float[] afloat = par1ArrayOfFloat;
        int i = par1ArrayOfFloat.length;

        for (int j = 0; j < i; ++j)
        {
            float f1 = afloat[j];
            nbttaglist.appendTag(new NBTTagFloat(f1));
        }

        return nbttaglist;
    }

    public EntityItem dropItem(Item p_145779_1_, int p_145779_2_)
    {
        return this.func_145778_a(p_145779_1_, p_145779_2_, 0.0F);
    }

    public EntityItem func_145778_a(Item p_145778_1_, int p_145778_2_, float p_145778_3_)
    {
        return this.entityDropItem(new ItemStack(p_145778_1_, p_145778_2_, 0), p_145778_3_);
    }

    /**
     * Drops an item at the position of the entity.
     */
    public EntityItem entityDropItem(ItemStack par1ItemStack, float par2)
    {
        if (par1ItemStack.stackSize != 0 && par1ItemStack.getItem() != null)
        {
            EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY + (double)par2, this.posZ, par1ItemStack);
            entityitem.delayBeforeCanPickup = 10;
            if (captureDrops)
            {
                capturedDrops.add(entityitem);
            }
            else
            {
                this.worldObj.spawnEntityInWorld(entityitem);
            }
            return entityitem;
        }
        else
        {
            return null;
        }
    }

    @SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return this.height / 2.0F;
    }

    /**
     * Checks whether target entity is alive.
     */
    public boolean isEntityAlive()
    {
        return !this.isDead;
    }

    /**
     * Checks if this entity is inside of an opaque block
     */
    public boolean isEntityInsideOpaqueBlock()
    {
        for (int i = 0; i < 8; ++i)
        {
            float f = ((float)((i >> 0) % 2) - 0.5F) * this.width * 0.8F;
            float f1 = ((float)((i >> 1) % 2) - 0.5F) * 0.1F;
            float f2 = ((float)((i >> 2) % 2) - 0.5F) * this.width * 0.8F;
            int j = MathHelper.floor_double(this.posX + (double)f);
            int k = MathHelper.floor_double(this.posY + (double)this.getEyeHeight() + (double)f1);
            int l = MathHelper.floor_double(this.posZ + (double)f2);

            if (this.worldObj.getBlock(j, k, l).isNormalCube())
            {
                return true;
            }
        }

        return false;
    }

    /**
     * First layer of player interaction
     */
    public boolean interactFirst(EntityPlayer par1EntityPlayer)
    {
        return false;
    }

    /**
     * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
     * pushable on contact, like boats or minecarts.
     */
    public AxisAlignedBB getCollisionBox(Entity par1Entity)
    {
        return null;
    }

    /**
     * Handles updating while being ridden by an entity
     */
    public void updateRidden()
    {
        if (this.ridingEntity.isDead)
        {
            this.ridingEntity = null;
        }
        else
        {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
            this.onUpdate();

            if (this.ridingEntity != null)
            {
                this.ridingEntity.updateRiderPosition();
                this.entityRiderYawDelta += (double)(this.ridingEntity.rotationYaw - this.ridingEntity.prevRotationYaw);

                for (this.entityRiderPitchDelta += (double)(this.ridingEntity.rotationPitch - this.ridingEntity.prevRotationPitch); this.entityRiderYawDelta >= 180.0D; this.entityRiderYawDelta -= 360.0D)
                {
                    ;
                }

                while (this.entityRiderYawDelta < -180.0D)
                {
                    this.entityRiderYawDelta += 360.0D;
                }

                while (this.entityRiderPitchDelta >= 180.0D)
                {
                    this.entityRiderPitchDelta -= 360.0D;
                }

                while (this.entityRiderPitchDelta < -180.0D)
                {
                    this.entityRiderPitchDelta += 360.0D;
                }

                double d0 = this.entityRiderYawDelta * 0.5D;
                double d1 = this.entityRiderPitchDelta * 0.5D;
                float f = 10.0F;

                if (d0 > (double)f)
                {
                    d0 = (double)f;
                }

                if (d0 < (double)(-f))
                {
                    d0 = (double)(-f);
                }

                if (d1 > (double)f)
                {
                    d1 = (double)f;
                }

                if (d1 < (double)(-f))
                {
                    d1 = (double)(-f);
                }

                this.entityRiderYawDelta -= d0;
                this.entityRiderPitchDelta -= d1;
            }
        }
    }

    public void updateRiderPosition()
    {
        if (this.riddenByEntity != null)
        {
            this.riddenByEntity.setPosition(this.posX, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ);
        }
    }

    /**
     * Returns the Y Offset of this entity.
     */
    public double getYOffset()
    {
        return (double)this.yOffset;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return (double)this.height * 0.75D;
    }

    /**
     * Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    public void mountEntity(Entity par1Entity)
    {
        this.entityRiderPitchDelta = 0.0D;
        this.entityRiderYawDelta = 0.0D;

        if (par1Entity == null)
        {
            if (this.ridingEntity != null)
            {
                this.setLocationAndAngles(this.ridingEntity.posX, this.ridingEntity.boundingBox.minY + (double)this.ridingEntity.height, this.ridingEntity.posZ, this.rotationYaw, this.rotationPitch);
                this.ridingEntity.riddenByEntity = null;
            }

            this.ridingEntity = null;
        }
        else
        {
            if (this.ridingEntity != null)
            {
                this.ridingEntity.riddenByEntity = null;
            }

            this.ridingEntity = par1Entity;
            par1Entity.riddenByEntity = this;
        }
    }

    /**
     * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9)
    {
        this.setPosition(par1, par3, par5);
        this.setRotation(par7, par8);
        List list = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.contract(0.03125D, 0.0D, 0.03125D));

        if (!list.isEmpty())
        {
            double d3 = 0.0D;

            for (int j = 0; j < list.size(); ++j)
            {
                AxisAlignedBB axisalignedbb = (AxisAlignedBB)list.get(j);

                if (axisalignedbb.maxY > d3)
                {
                    d3 = axisalignedbb.maxY;
                }
            }

            par3 += d3 - this.boundingBox.minY;
            this.setPosition(par1, par3, par5);
        }
    }

    public float getCollisionBorderSize()
    {
        return 0.1F;
    }

    /**
     * returns a (normalized) vector of where this entity is looking
     */
    public Vec3 getLookVec()
    {
        return null;
    }

    /**
     * Called by portal blocks when an entity is within it.
     */
    public void setInPortal()
    {
        if (this.timeUntilPortal > 0)
        {
            this.timeUntilPortal = this.getPortalCooldown();
        }
        else
        {
            double d0 = this.prevPosX - this.posX;
            double d1 = this.prevPosZ - this.posZ;

            if (!this.worldObj.isRemote && !this.inPortal)
            {
                this.teleportDirection = Direction.getMovementDirection(d0, d1);
            }

            this.inPortal = true;
        }
    }

    /**
     * Return the amount of cooldown before this entity can use a portal again.
     */
    public int getPortalCooldown()
    {
        return 300;
    }

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    @SideOnly(Side.CLIENT)
    public void setVelocity(double par1, double par3, double par5)
    {
        this.motionX = par1;
        this.motionY = par3;
        this.motionZ = par5;
    }

    @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(byte par1) {}

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    @SideOnly(Side.CLIENT)
    public void performHurtAnimation() {}

    public ItemStack[] getLastActiveItems()
    {
        return null;
    }

    /**
     * Sets the held item, or an armor slot. Slot 0 is held item. Slot 1-4 is armor. Params: Item, slot
     */
    public void setCurrentItemOrArmor(int par1, ItemStack par2ItemStack) {}

    /**
     * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
     */
    public boolean isBurning()
    {
        boolean flag = this.worldObj != null && this.worldObj.isRemote;
        return !this.isImmuneToFire && (this.fire > 0 || flag && this.getFlag(0));
    }

    /**
     * Returns true if the entity is riding another entity, used by render to rotate the legs to be in 'sit' position
     * for players.
     */
    public boolean isRiding()
    {
        return this.ridingEntity != null && ridingEntity.shouldRiderSit();
    }

    /**
     * Returns if this entity is sneaking.
     */
    public boolean isSneaking()
    {
        return this.getFlag(1);
    }

    /**
     * Sets the sneaking flag.
     */
    public void setSneaking(boolean par1)
    {
        this.setFlag(1, par1);
    }

    /**
     * Get if the Entity is sprinting.
     */
    public boolean isSprinting()
    {
        return this.getFlag(3);
    }

    /**
     * Set sprinting switch for Entity.
     */
    public void setSprinting(boolean par1)
    {
        this.setFlag(3, par1);
    }

    public boolean isInvisible()
    {
        return this.getFlag(5);
    }

    /**
     * Only used by renderer in EntityLivingBase subclasses.nDetermines if an entity is visible or not to a specfic
     * player, if the entity is normally invisible.nFor EntityLivingBase subclasses, returning false when invisible will
     * render the entity semitransparent.
     */
    @SideOnly(Side.CLIENT)
    public boolean isInvisibleToPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.isInvisible();
    }

    public void setInvisible(boolean par1)
    {
        this.setFlag(5, par1);
    }

    @SideOnly(Side.CLIENT)
    public boolean isEating()
    {
        return this.getFlag(4);
    }

    public void setEating(boolean par1)
    {
        this.setFlag(4, par1);
    }

    /**
     * Returns true if the flag is active for the entity. Known flags: 0) is burning; 1) is sneaking; 2) is riding
     * something; 3) is sprinting; 4) is eating
     */
    protected boolean getFlag(int par1)
    {
        return (this.dataWatcher.getWatchableObjectByte(0) & 1 << par1) != 0;
    }

    /**
     * Enable or disable a entity flag, see getEntityFlag to read the know flags.
     */
    protected void setFlag(int par1, boolean par2)
    {
        byte b0 = this.dataWatcher.getWatchableObjectByte(0);

        if (par2)
        {
            this.dataWatcher.updateObject(0, Byte.valueOf((byte)(b0 | 1 << par1)));
        }
        else
        {
            this.dataWatcher.updateObject(0, Byte.valueOf((byte)(b0 & ~(1 << par1))));
        }
    }

    public int getAir()
    {
        return this.dataWatcher.getWatchableObjectShort(1);
    }

    public void setAir(int par1)
    {
        this.dataWatcher.updateObject(1, Short.valueOf((short)par1));
    }

    /**
     * Called when a lightning bolt hits the entity.
     */
    public void onStruckByLightning(EntityLightningBolt par1EntityLightningBolt)
    {
        this.dealFireDamage(5);
        ++this.fire;

        if (this.fire == 0)
        {
            this.setFire(8);
        }
    }

    /**
     * This method gets called when the entity kills another one.
     */
    public void onKillEntity(EntityLivingBase par1EntityLivingBase) {}

    protected boolean func_145771_j(double p_145771_1_, double p_145771_3_, double p_145771_5_)
    {
        int i = MathHelper.floor_double(p_145771_1_);
        int j = MathHelper.floor_double(p_145771_3_);
        int k = MathHelper.floor_double(p_145771_5_);
        double d3 = p_145771_1_ - (double)i;
        double d4 = p_145771_3_ - (double)j;
        double d5 = p_145771_5_ - (double)k;
        List list = this.worldObj.func_147461_a(this.boundingBox);

        if (list.isEmpty() && !this.worldObj.func_147469_q(i, j, k))
        {
            return false;
        }
        else
        {
            boolean flag = !this.worldObj.func_147469_q(i - 1, j, k);
            boolean flag1 = !this.worldObj.func_147469_q(i + 1, j, k);
            boolean flag2 = !this.worldObj.func_147469_q(i, j - 1, k);
            boolean flag3 = !this.worldObj.func_147469_q(i, j + 1, k);
            boolean flag4 = !this.worldObj.func_147469_q(i, j, k - 1);
            boolean flag5 = !this.worldObj.func_147469_q(i, j, k + 1);
            byte b0 = 3;
            double d6 = 9999.0D;

            if (flag && d3 < d6)
            {
                d6 = d3;
                b0 = 0;
            }

            if (flag1 && 1.0D - d3 < d6)
            {
                d6 = 1.0D - d3;
                b0 = 1;
            }

            if (flag3 && 1.0D - d4 < d6)
            {
                d6 = 1.0D - d4;
                b0 = 3;
            }

            if (flag4 && d5 < d6)
            {
                d6 = d5;
                b0 = 4;
            }

            if (flag5 && 1.0D - d5 < d6)
            {
                d6 = 1.0D - d5;
                b0 = 5;
            }

            float f = this.rand.nextFloat() * 0.2F + 0.1F;

            if (b0 == 0)
            {
                this.motionX = (double)(-f);
            }

            if (b0 == 1)
            {
                this.motionX = (double)f;
            }

            if (b0 == 2)
            {
                this.motionY = (double)(-f);
            }

            if (b0 == 3)
            {
                this.motionY = (double)f;
            }

            if (b0 == 4)
            {
                this.motionZ = (double)(-f);
            }

            if (b0 == 5)
            {
                this.motionZ = (double)f;
            }

            return true;
        }
    }

    /**
     * Sets the Entity inside a web block.
     */
    public void setInWeb()
    {
        this.isInWeb = true;
        this.fallDistance = 0.0F;
    }

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getCommandSenderName()
    {
        String s = EntityList.getEntityString(this);

        if (s == null)
        {
            s = "generic";
        }

        return StatCollector.translateToLocal("entity." + s + ".name");
    }

    /**
     * Return the Entity parts making up this Entity (currently only for dragons)
     */
    public Entity[] getParts()
    {
        return null;
    }

    /**
     * Returns true if Entity argument is equal to this Entity
     */
    public boolean isEntityEqual(Entity par1Entity)
    {
        return this == par1Entity;
    }

    public float getRotationYawHead()
    {
        return 0.0F;
    }

    /**
     * Sets the head's yaw rotation of the entity.
     */
    @SideOnly(Side.CLIENT)
    public void setRotationYawHead(float par1) {}

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem()
    {
        return true;
    }

    /**
     * Called when a player attacks an entity. If this returns true the attack will not happen.
     */
    public boolean hitByEntity(Entity par1Entity)
    {
        return false;
    }

    public String toString()
    {
        return String.format("%s[\'%s\'/%d, l=\'%s\', x=%.2f, y=%.2f, z=%.2f]", new Object[] {this.getClass().getSimpleName(), this.getCommandSenderName(), Integer.valueOf(this.entityId), this.worldObj == null ? "~NULL~" : this.worldObj.getWorldInfo().getWorldName(), Double.valueOf(this.posX), Double.valueOf(this.posY), Double.valueOf(this.posZ)});
    }

    /**
     * Return whether this entity is invulnerable to damage.
     */
    public boolean isEntityInvulnerable()
    {
        return this.invulnerable;
    }

    /**
     * Sets this entity's location and angles to the location and angles of the passed in entity.
     */
    public void copyLocationAndAnglesFrom(Entity par1Entity)
    {
        this.setLocationAndAngles(par1Entity.posX, par1Entity.posY, par1Entity.posZ, par1Entity.rotationYaw, par1Entity.rotationPitch);
    }

    /**
     * Copies important data from another entity to this entity. Used when teleporting entities between worlds, as this
     * actually deletes the teleporting entity and re-creates it on the other side. Params: Entity to copy from, unused
     * (always true)
     */
    public void copyDataFrom(Entity par1Entity, boolean par2)
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        par1Entity.writeToNBT(nbttagcompound);
        this.readFromNBT(nbttagcompound);
        this.timeUntilPortal = par1Entity.timeUntilPortal;
        this.teleportDirection = par1Entity.teleportDirection;
    }

    /**
     * Teleports the entity to another dimension. Params: Dimension number to teleport to
     */
    public void travelToDimension(int par1)
    {
        if (!this.worldObj.isRemote && !this.isDead)
        {
            this.worldObj.theProfiler.startSection("changeDimension");
            MinecraftServer minecraftserver = MinecraftServer.getServer();
            int j = this.dimension;
            WorldServer worldserver = minecraftserver.worldServerForDimension(j);
            WorldServer worldserver1 = minecraftserver.worldServerForDimension(par1);
            this.dimension = par1;

            if (j == 1 && par1 == 1)
            {
                worldserver1 = minecraftserver.worldServerForDimension(0);
                this.dimension = 0;
            }

            this.worldObj.removeEntity(this);
            this.isDead = false;
            this.worldObj.theProfiler.startSection("reposition");
            minecraftserver.getConfigurationManager().transferEntityToWorld(this, j, worldserver, worldserver1);
            this.worldObj.theProfiler.endStartSection("reloading");
            Entity entity = EntityList.createEntityByName(EntityList.getEntityString(this), worldserver1);

            if (entity != null)
            {
                entity.copyDataFrom(this, true);

                if (j == 1 && par1 == 1)
                {
                    ChunkCoordinates chunkcoordinates = worldserver1.getSpawnPoint();
                    chunkcoordinates.posY = this.worldObj.getTopSolidOrLiquidBlock(chunkcoordinates.posX, chunkcoordinates.posZ);
                    entity.setLocationAndAngles((double)chunkcoordinates.posX, (double)chunkcoordinates.posY, (double)chunkcoordinates.posZ, entity.rotationYaw, entity.rotationPitch);
                }

                worldserver1.spawnEntityInWorld(entity);
            }

            this.isDead = true;
            this.worldObj.theProfiler.endSection();
            worldserver.resetUpdateEntityTick();
            worldserver1.resetUpdateEntityTick();
            this.worldObj.theProfiler.endSection();
        }
    }

    public float func_145772_a(Explosion p_145772_1_, World p_145772_2_, int p_145772_3_, int p_145772_4_, int p_145772_5_, Block p_145772_6_)
    {
        return p_145772_6_.getExplosionResistance(this, p_145772_2_, p_145772_3_, p_145772_3_, p_145772_4_, posX, posY + getEyeHeight(), posZ);
    }

    public boolean func_145774_a(Explosion p_145774_1_, World p_145774_2_, int p_145774_3_, int p_145774_4_, int p_145774_5_, Block p_145774_6_, float p_145774_7_)
    {
        return true;
    }

    /**
     * The number of iterations PathFinder.getSafePoint will execute before giving up.
     */
    public int getMaxSafePointTries()
    {
        return 3;
    }

    public int getTeleportDirection()
    {
        return this.teleportDirection;
    }

    /**
     * Return whether this entity should NOT trigger a pressure plate or a tripwire.
     */
    public boolean doesEntityNotTriggerPressurePlate()
    {
        return false;
    }

    public void addEntityCrashInfo(CrashReportCategory par1CrashReportCategory)
    {
        par1CrashReportCategory.addCrashSectionCallable("Entity Type", new Callable()
        {
            private static final String __OBFID = "CL_00001534";
            public String call()
            {
                return EntityList.getEntityString(Entity.this) + " (" + Entity.this.getClass().getCanonicalName() + ")";
            }
        });
        par1CrashReportCategory.addCrashSection("Entity ID", Integer.valueOf(this.entityId));
        par1CrashReportCategory.addCrashSectionCallable("Entity Name", new Callable()
        {
            private static final String __OBFID = "CL_00001535";
            public String call()
            {
                return Entity.this.getCommandSenderName();
            }
        });
        par1CrashReportCategory.addCrashSection("Entity\'s Exact location", String.format("%.2f, %.2f, %.2f", new Object[] {Double.valueOf(this.posX), Double.valueOf(this.posY), Double.valueOf(this.posZ)}));
        par1CrashReportCategory.addCrashSection("Entity\'s Block location", CrashReportCategory.getLocationInfo(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)));
        par1CrashReportCategory.addCrashSection("Entity\'s Momentum", String.format("%.2f, %.2f, %.2f", new Object[] {Double.valueOf(this.motionX), Double.valueOf(this.motionY), Double.valueOf(this.motionZ)}));
    }

    /**
     * Return whether this entity should be rendered as on fire.
     */
    @SideOnly(Side.CLIENT)
    public boolean canRenderOnFire()
    {
        return this.isBurning();
    }

    public UUID getUniqueID()
    {
        return this.entityUniqueID;
    }

    public boolean isPushedByWater()
    {
        return true;
    }

    public IChatComponent func_145748_c_()
    {
        return new ChatComponentText(this.getCommandSenderName());
    }

    public void func_145781_i(int p_145781_1_) {}

    /* ================================== Forge Start =====================================*/
    /**
     * Returns a NBTTagCompound that can be used to store custom data for this entity.
     * It will be written, and read from disc, so it persists over world saves.
     * @return A NBTTagCompound
     */
    public NBTTagCompound getEntityData()
    {
        if (customEntityData == null)
        {
            customEntityData = new NBTTagCompound();
        }
        return customEntityData;
    }

    /**
     * Used in model rendering to determine if the entity riding this entity should be in the 'sitting' position.
     * @return false to prevent an entity that is mounted to this entity from displaying the 'sitting' animation.
     */
    public boolean shouldRiderSit()
    {
        return true;
    }

    /**
     * Called when a user uses the creative pick block button on this entity.
     *
     * @param target The full target the player is looking at
     * @return A ItemStack to add to the player's inventory, Null if nothing should be added.
     */
    public ItemStack getPickedResult(MovingObjectPosition target)
    {
        if (this instanceof EntityPainting)
        {
            return new ItemStack(Items.painting);
        }
        else if (this instanceof EntityLeashKnot)
        {
            return new ItemStack(Items.lead);
        }
        else if (this instanceof EntityItemFrame)
        {
            ItemStack held = ((EntityItemFrame)this).getDisplayedItem();
            if (held == null)
            {
                return new ItemStack(Items.item_frame);
            }
            else
            {
                return held.copy();
            }
        }
        else if (this instanceof EntityMinecart)
        {
            return ((EntityMinecart)this).getCartItem();
        }
        else if (this instanceof EntityBoat)
        {
            return new ItemStack(Items.boat);
        }
        else
        {
            int id = EntityList.getEntityID(this);
            if (id > 0 && EntityList.entityEggs.containsKey(id))
            {
                return new ItemStack(Items.spawn_egg, 1, id);
            }
        }
        return null;
    }

    public UUID getPersistentID()
    {
        return entityUniqueID;
    }

    /**
     * Reset the entity ID to a new value. Not to be used from Mod code
     */
    public final void resetEntityId()
    {
        this.entityId = nextEntityID++;
    }

    public boolean shouldRenderInPass(int pass)
    {
        return pass == 0;
    }

    /**
     * Returns true if the entity is of the @link{EnumCreatureType} provided
     * @param type The EnumCreatureType type this entity is evaluating
     * @param forSpawnCount If this is being invoked to check spawn count caps.
     * @return If the creature is of the type provided
     */
    public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount)
    {
        return type.getCreatureClass().isAssignableFrom(this.getClass());
    }

    /**
     * Register the instance of IExtendedProperties into the entity's collection.
     * @param identifier The identifier which you can use to retrieve these properties for the entity.
     * @param properties The instanceof IExtendedProperties to register
     * @return The identifier that was used to register the extended properties.  Empty String indicates an error.  If your requested key already existed, this will return a modified one that is unique.
     */
    public String registerExtendedProperties(String identifier, IExtendedEntityProperties properties)
    {
        if (identifier == null)
        {
            FMLLog.warning("Someone is attempting to register extended properties using a null identifier.  This is not allowed.  Aborting.  This may have caused instability.");
            return "";
        }
        if (properties == null)
        {
            FMLLog.warning("Someone is attempting to register null extended properties.  This is not allowed.  Aborting.  This may have caused instability.");
            return "";
        }

        String baseIdentifier = identifier;
        int identifierModCount = 1;
        while (this.extendedProperties.containsKey(identifier))
        {
            identifier = String.format("%s%d", baseIdentifier, identifierModCount++);
        }

        if (baseIdentifier != identifier)
        {
            FMLLog.info("An attempt was made to register exended properties using an existing key.  The duplicate identifier (%s) has been remapped to %s.", baseIdentifier, identifier);
        }

        this.extendedProperties.put(identifier, properties);
        return identifier;
    }

    /**
     * Gets the extended properties identified by the passed in key
     * @param identifier The key that identifies the extended properties.
     * @return The instance of IExtendedProperties that was found, or null.
     */
    public IExtendedEntityProperties getExtendedProperties(String identifier)
    {
        return this.extendedProperties.get(identifier);
    }

    /**
     * If a rider of this entity can interact with this entity. Should return true on the
     * ridden entity if so.
     *
     * @return if the entity can be interacted with from a rider
     */
    public boolean canRiderInteract()
    {
        return false;
    }

    /**
     * If the rider should be dismounted from the entity when the entity goes under water
     *
     * @param rider The entity that is riding
     * @return if the entity should be dismounted when under water
     */
    public boolean shouldDismountInWater(Entity rider)
    {
        return this instanceof EntityLivingBase;
    }
    /* ================================== Forge End =====================================*/

    public static enum EnumEntitySize
    {
        SIZE_1,
        SIZE_2,
        SIZE_3,
        SIZE_4,
        SIZE_5,
        SIZE_6;

        private static final String __OBFID = "CL_00001537";

        public int multiplyBy32AndRound(double par1)
        {
            double d1 = par1 - ((double)MathHelper.floor_double(par1) + 0.5D);

            switch (Entity.SwitchEnumEntitySize.field_96565_a[this.ordinal()])
            {
                case 1:
                    if (d1 < 0.0D)
                    {
                        if (d1 < -0.3125D)
                        {
                            return MathHelper.ceiling_double_int(par1 * 32.0D);
                        }
                    }
                    else if (d1 < 0.3125D)
                    {
                        return MathHelper.ceiling_double_int(par1 * 32.0D);
                    }

                    return MathHelper.floor_double(par1 * 32.0D);
                case 2:
                    if (d1 < 0.0D)
                    {
                        if (d1 < -0.3125D)
                        {
                            return MathHelper.floor_double(par1 * 32.0D);
                        }
                    }
                    else if (d1 < 0.3125D)
                    {
                        return MathHelper.floor_double(par1 * 32.0D);
                    }

                    return MathHelper.ceiling_double_int(par1 * 32.0D);
                case 3:
                    if (d1 > 0.0D)
                    {
                        return MathHelper.floor_double(par1 * 32.0D);
                    }

                    return MathHelper.ceiling_double_int(par1 * 32.0D);
                case 4:
                    if (d1 < 0.0D)
                    {
                        if (d1 < -0.1875D)
                        {
                            return MathHelper.ceiling_double_int(par1 * 32.0D);
                        }
                    }
                    else if (d1 < 0.1875D)
                    {
                        return MathHelper.ceiling_double_int(par1 * 32.0D);
                    }

                    return MathHelper.floor_double(par1 * 32.0D);
                case 5:
                    if (d1 < 0.0D)
                    {
                        if (d1 < -0.1875D)
                        {
                            return MathHelper.floor_double(par1 * 32.0D);
                        }
                    }
                    else if (d1 < 0.1875D)
                    {
                        return MathHelper.floor_double(par1 * 32.0D);
                    }

                    return MathHelper.ceiling_double_int(par1 * 32.0D);
                case 6:
                default:
                    if (d1 > 0.0D)
                    {
                        return MathHelper.ceiling_double_int(par1 * 32.0D);
                    }
                    else
                    {
                        return MathHelper.floor_double(par1 * 32.0D);
                    }
            }
        }
    }

    static final class SwitchEnumEntitySize
        {
            static final int[] field_96565_a = new int[Entity.EnumEntitySize.values().length];
            private static final String __OBFID = "CL_00001536";

            static
            {
                try
                {
                    field_96565_a[Entity.EnumEntitySize.SIZE_1.ordinal()] = 1;
                }
                catch (NoSuchFieldError var6)
                {
                    ;
                }

                try
                {
                    field_96565_a[Entity.EnumEntitySize.SIZE_2.ordinal()] = 2;
                }
                catch (NoSuchFieldError var5)
                {
                    ;
                }

                try
                {
                    field_96565_a[Entity.EnumEntitySize.SIZE_3.ordinal()] = 3;
                }
                catch (NoSuchFieldError var4)
                {
                    ;
                }

                try
                {
                    field_96565_a[Entity.EnumEntitySize.SIZE_4.ordinal()] = 4;
                }
                catch (NoSuchFieldError var3)
                {
                    ;
                }

                try
                {
                    field_96565_a[Entity.EnumEntitySize.SIZE_5.ordinal()] = 5;
                }
                catch (NoSuchFieldError var2)
                {
                    ;
                }

                try
                {
                    field_96565_a[Entity.EnumEntitySize.SIZE_6.ordinal()] = 6;
                }
                catch (NoSuchFieldError var1)
                {
                    ;
                }
            }
        }
}