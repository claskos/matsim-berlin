import numpy as np
import matplotlib.pyplot as plt
import glob
import seaborn as sns

files_cold = sorted(glob.glob("*cold.txt"))
files_warm = sorted(glob.glob("*warm.txt"))

emissions_cold = []
emissions_warm = []

for f in files_cold:
    arr = np.loadtxt(f, dtype=float)
    emissions_cold.append(np.sum(arr))
    
for f in files_warm:
    arr = np.loadtxt(f, dtype=float)
    emissions_warm.append(np.sum(arr))


plt.figure()
sns.barplot(x = np.arange(len(emissions_cold)), y = emissions_cold)
plt.suptitle("Cold CO2 Emissions")
plt.xlabel("Scenario")
plt.ylabel("CO2 Emissions")
plt.xticks(ticks=[0,1,2,3,4,5], labels=["Baseline", "Case 1", "Case 2", "Case 3", "Case 4", "Case 5"])
plt.show()


plt.figure()
sns.barplot(x = np.arange(len(emissions_warm)), y = emissions_warm)
plt.suptitle("Warm CO2 Emissions")
plt.xlabel("Scenario")
plt.ylabel("CO2 Emissions")
plt.xticks(ticks=[0,1,2,3,4,5], labels=["Baseline", "Case 1", "Case 2", "Case 3", "Case 4", "Case 5"])
plt.show()