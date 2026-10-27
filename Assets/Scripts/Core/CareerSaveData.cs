using System;
using System.Collections.Generic;

namespace FootballEmpire.Core
{
    [Serializable]
    public sealed class CareerSaveData
    {
        public int schemaVersion = 1;
        public string managerName = "Nowy Menedżer";
        public string season = "2026/27";
        public string clubId = "fc-empire";
        public int currentRound = 1;
        public int matchesPlayed;
        public int wins;
        public int draws;
        public int losses;
        public int goalsFor;
        public int goalsAgainst;
        public List<MatchResult> recentResults = new List<MatchResult>();
    }
}
