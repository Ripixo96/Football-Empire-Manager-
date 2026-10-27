using System;
using System.Collections.Generic;
using UnityEngine;

namespace FootballEmpire.Core
{
    [Serializable]
    public sealed class ClubData
    {
        public string id;
        public string clubName = "FC Empire";
        public string country = "Polska";
        public string leagueName = "Liga krajowa";
        public int reputation = 50;
        public long transferBudget = 5000000;
        public long wageBudget = 100000;
        public List<PlayerData> squad = new List<PlayerData>();

        public int AverageOverall
        {
            get
            {
                if (squad == null || squad.Count == 0) return 0;
                int total = 0;
                foreach (PlayerData player in squad)
                    if (player != null) total += player.overall;
                return total / squad.Count;
            }
        }
    }
}
