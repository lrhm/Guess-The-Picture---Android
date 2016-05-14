package ir.treeco.aftabe.Util;

import java.util.ArrayList;

/**
 * Created by al on 3/7/16.
 */
public class LevelCalculator {

    private int mScore;
    private ArrayList<Integer> scoreLevels;

    public LevelCalculator(int score) {
        mScore = score + 3; // 3 for big 3 dummies

        scoreLevels = new ArrayList<>();
        scoreLevels.add(0);
        scoreLevels.add(8);
        int last = 8;
        while (last <= mScore) {
            last += 8;
            scoreLevels.add(last);
        }
    }

    public int getLevel() {
        int level = 0;
        for (Integer integer : scoreLevels) {
            if (mScore <= integer)
                break;
            level++;
        }
        return level;
    }

    public int getExp() {
        int level = getLevel();
        if (level <= 0)
            return 0;
        int lvlScore = scoreLevels.get(level);
        int scoreDiff = mScore - scoreLevels.get(level - 1);
        double percent = scoreDiff / (double) lvlScore;
        return (int) (percent * 8);
    }
}
