import math


def getNextLvlExp(currLvl):
    baseExp = 2500
    nextExp = baseExp + (1750 * (currLvl - 1))
    return nextExp

sum = 0
max_level = 99

for i in range(1, max_level):
    nextExpNeeded = getNextLvlExp(i)
    print("Exp for level", i, ":", nextExpNeeded)
    sum += nextExpNeeded

print("Total EXP needed to reach", max_level, ":", sum)

expPerDifficulty = {
    "Easy" : 100,
    "Medium": 250,
    "Hard": 600,
    "Expert": 1500
}

for difficulty in expPerDifficulty:
    numQuestsUntilMaxLvl = math.ceil(sum / expPerDifficulty[difficulty])
    print("#", difficulty, "Quests to complete to reach max level :", numQuestsUntilMaxLvl)