using System;
using UnityEngine;

namespace FootballEmpire.Core
{
    public static class CareerSaveSystem
    {
        private const string SaveKey = "football_empire_career_v1";

        public static void Save(CareerSaveData data)
        {
            if (data == null) throw new ArgumentNullException(nameof(data));
            PlayerPrefs.SetString(SaveKey, JsonUtility.ToJson(data));
            PlayerPrefs.Save();
        }

        public static CareerSaveData Load()
        {
            if (!PlayerPrefs.HasKey(SaveKey)) return new CareerSaveData();
            string json = PlayerPrefs.GetString(SaveKey, string.Empty);
            if (string.IsNullOrWhiteSpace(json)) return new CareerSaveData();

            try
            {
                CareerSaveData data = JsonUtility.FromJson<CareerSaveData>(json);
                return data ?? new CareerSaveData();
            }
            catch (Exception exception)
            {
                Debug.LogWarning("Football Empire save could not be read: " + exception.Message);
                return new CareerSaveData();
            }
        }

        public static void Delete()
        {
            PlayerPrefs.DeleteKey(SaveKey);
            PlayerPrefs.Save();
        }
    }
}
