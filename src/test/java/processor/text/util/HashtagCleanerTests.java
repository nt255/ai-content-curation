package test.java.processor.text.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.inject.Inject;

import main.java.processor.text.util.HashtagCleaner;
import test.java.TestWithInjections;

public class HashtagCleanerTests extends TestWithInjections {

    @Inject private HashtagCleaner cleaner;

    
    @Test
    void aquariums() {
        String s = "Appeal to parents\r\n"
                + " with kids who want to learn about marine life.\r\n"
                + "\r\n"
                + "1. #Aquariums\r\n"
                + "2. #1FishTankFun\r\n"
                + "3. #AquaticAdventures\r\n"
                + "4. #AquaticAdventures\r\n"
                + "5. #FamilyFunTime\r\n"
                + "6. #AquaticExplorations\r\n"
                + "7. #KidFriendlyAquariums\r\n"
                + "8. #FamilyBonding\r\n"
                + "9. #AquaticLearning\r\n"
                + "10. #FamilyTimeTogether";
        
        assertEquals("#Aquariums #AquaticAdventures #FamilyFunTime "
                + "#AquaticExplorations #KidFriendlyAquariums "
                + "#FamilyBonding #AquaticLearning", cleaner.clean(s, 7, false));
    }
    
    @Test
    void knitting() {
        String s = "#KnittingLove: Celebrating the joy and passion for knitting"
                + " among women.\\n2. #StitchingSisters: A community of "
                + "self-sufficient women who find solace and creativity in "
                + "knitting.\\n3. #CraftyWomen: Empowering women with knitting "
                + "skills for self-sufficiency and creative fulfillment.\\n4. "
                + "#KnitInspiration: Inspiring women to unlock their potential "
                + "through the art of knitting.\\n5. #FiberFridays: "
                + "Join the movement of women embracing knitting as a source of"
                + " self-sufficiency and relaxation.\\n6. #EmpoweredKnitters: "
                + "Women taking control of their lives through the satisfaction"
                + " and independence found in knitting.\\n7. "
                + "#KnittingCommunity: A supportive network of women empowering"
                + "each other through knitting and self-sufficiency.\\n8. "
                + "#ThreadedWithResilience: Uniting women through knitting, sho"
                + "wcasing their strength and self-sufficiency.\\n9. #WomenWhoK"
                + "nit: Celebrating the timeless craft that fosters self-suffic"
                + "iency and connects women across generations.\\n10. #Knitting"
                + "Revolution: Join the revolution of women reclaiming self-suf"
                + "ficiency and creativity through knitting.";
        
        assertEquals("#KnittingLove #StitchingSisters #CraftyWomen "
                + "#KnitInspiration #FiberFridays #EmpoweredKnitters "
                + "#KnittingCommunity #ThreadedWithResilience "
                + "#WomenWhoKnit", cleaner.clean(s, 9, false));
    }
    
    
    @Test
    void fitness() {
        String s = "#BeastModeOn\\n#StrongerEveryDay\\n#FitForLife\\n#TrainHard"
                + "WinHard\\n#SweatItOut\\n#BuiltNotBought\\n#NoPainNoGain\\n#G"
                + "etRipped\\n#GymLife\\n#FitnessJourney\\n#FitMen\\n#MenWhoLif"
                + "t\\n#MuscleUp\\n#ShredIt\\n#FitnessGoals\\n#LifestyleOfAesth"
                + "etics\\n#FitAndFly\\n#AlphaFitness\\n#GainzOnly\\n#BeachBody"
                + "Ready";
        
        assertEquals("#BeastModeOn #StrongerEveryDay #FitForLife "
                + "#TrainHardWinHard #SweatItOut #BuiltNotBought "
                + "#NoPainNoGain #GetRipped #GymLife #FitnessJourney "
                + "#FitMen #MenWhoLift #MuscleUp #ShredIt #FitnessGoals "
                + "#LifestyleOfAesthetics #FitAndFly #AlphaFitness "
                + "#GainzOnly", cleaner.clean(s, 19, false));
    }
    
}
