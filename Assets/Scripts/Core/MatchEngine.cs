using System;
using UnityEngine;

namespace FootballEmpire.Core
{
    [Serializable]
    public sealed class MatchResult
    {
        public string homeTeam;
        public string awayTeam;
        public int homeGoals;
        public int awayGoals;
        public int homeShots;
        public int awayShots;
        public int homePossession;
        public int awayPossession;
        public int homeYellowCards;
        public int awayYellowCards;
        public bool wasPlayed;
        public string Summary => homeTeam + " " + homeGoals + " : " + awayGoals + " " + awayTeam;
    }

    public static class MatchEngine
    {
        // First playable foundation: score and match statistics are calculated from team strength.
        // Later milestones will replace this with a minute-by-minute 2D pitch simulation.
        public static MatchResult Simulate(string homeName, int homeStrength, string awayName, int awayStrength, int randomSeed = -1)
        {
            if (randomSeed >= 0) UnityEngine.Random.InitState(randomSeed);

            homeStrength = Mathf.Clamp(homeStrength, 1, 99);
            awayStrength = Mathf.Clamp(awayStrength, 1, 99);

            float homeChance = 0.85f + (homeStrength - awayStrength) * 0.018f;
            float awayChance = 0.85f + (awayStrength - homeStrength) * 0.018f;

            int homeShots = Mathf.Clamp(Mathf.RoundToInt(UnityEngine.Random.Range(4f, 16f) * Mathf.Max(0.45f, homeChance)), 1, 30);
            int awayShots = Mathf.Clamp(Mathf.RoundToInt(UnityEngine.Random.Range(4f, 16f) * Mathf.Max(0.45f, awayChance)), 1, 30);
            int homeGoals = GoalsFromShots(homeShots, homeStrength, awayStrength);
            int awayGoals = GoalsFromShots(awayShots, awayStrength, homeStrength);
            int homePossession = Mathf.Clamp(50 + (homeStrength - awayStrength) / 3 + UnityEngine.Random.Range(-8, 9), 25, 75);

            return new MatchResult
            {
                homeTeam = string.IsNullOrWhiteSpace(homeName) ? "Home" : homeName,
                awayTeam = string.IsNullOrWhiteSpace(awayName) ? "Away" : awayName,
                homeGoals = homeGoals,
                awayGoals = awayGoals,
                homeShots = homeShots,
                awayShots = awayShots,
                homePossession = homePossession,
                awayPossession = 100 - homePossession,
                homeYellowCards = UnityEngine.Random.Range(0, 4),
                awayYellowCards = UnityEngine.Random.Range(0, 4),
                wasPlayed = true
            };
        }

        private static int GoalsFromShots(int shots, int attack, int defence)
        {
            float conversion = Mathf.Clamp(0.09f + (attack - defence) * 0.0015f, 0.04f, 0.22f);
            int goals = 0;
            for (int i = 0; i < shots; i++)
                if (UnityEngine.Random.value < conversion) goals++;
            return Mathf.Clamp(goals, 0, 9);
        }
    }
}
