package mods.flammpfeil.slashblade;

import net.minecraftforge.common.ForgeConfigSpec;

public class SlashBladeConfig {
    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec.BooleanValue HUNGER_CAN_REPAIR;
    public static ForgeConfigSpec.IntValue MAX_PROUD_SOUL_GOT;
    public static ForgeConfigSpec.IntValue SUMMON_SWORD_COST;
    public static ForgeConfigSpec.IntValue SUMMON_SWORD_ART_COST;
    public static ForgeConfigSpec.DoubleValue BEWITCHED_HUNGER_EXHAUSTION;
    public static ForgeConfigSpec.DoubleValue REFINE_BOUNS;
    
    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        COMMON_BUILDER.comment("General settings").push("general");
        
        HUNGER_CAN_REPAIR = COMMON_BUILDER.comment("Determines whether to make hunger effect repair slashblade.",
                "If enable, if player has hunger effect, your slashblade in hotbar will be repaired, cost player's hunger.")
                .define("hunger_can_repair", true);
        
        SUMMON_SWORD_COST = COMMON_BUILDER
                .comment("Determining the proud soul cost for single summon mirage blade.")
                .defineInRange("summon_sword_cost", 2, 1, Integer.MAX_VALUE);
        SUMMON_SWORD_ART_COST = COMMON_BUILDER
                .comment("Determining the proud soul cost for summon blade arts.")
                .defineInRange("max_proud_soul_got", 50, 1, Integer.MAX_VALUE);
        
        MAX_PROUD_SOUL_GOT = COMMON_BUILDER
                .comment("Determining the max proud soul count for single mobs kill.")
                .defineInRange("max_proud_soul_got", 100, 1, Integer.MAX_VALUE);
        BEWITCHED_HUNGER_EXHAUSTION = COMMON_BUILDER
                .comment("Determining the base exhaustion for slashblade's self-repair.")
                .defineInRange("bewitched_hunger_exhaustion", 0.01D, 0.0001D, Double.MAX_VALUE);
        
        REFINE_BOUNS = COMMON_BUILDER
                .comment("Determining the base refine bouns damage.", 
                        "If the slashblade has fiercer edge type(KillCount >=1000), bouns will always max.")
                .defineInRange("refine_bouns", 0.75D, 0.0D, 1.0D);
        
        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }
}
