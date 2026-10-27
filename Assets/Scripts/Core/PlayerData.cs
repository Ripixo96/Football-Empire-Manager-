using System;
using UnityEngine;

namespace FootballEmpire.Core
{
    [Serializable]
    public sealed class PlayerData
    {
        public string id;
        public string firstName;
        public string lastName;
        public int age = 20;
        public string position = "CM";
        [Range(1, 99)] public int overall = 60;
        [Range(1, 99)] public int potential = 75;
        public int pace = 60;
        public int shooting = 55;
        public int passing = 60;
        public int defending = 50;
        public int physical = 60;
        public int morale = 75;
        public int fitness = 100;
        public int marketValue = 500000;

        public string FullName => string.IsNullOrWhiteSpace(firstName) && string.IsNullOrWhiteSpace(lastName)
            ? "Unknown Player"
            : (firstName + " " + lastName).Trim();
    }
}
