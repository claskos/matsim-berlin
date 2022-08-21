import numpy as np
import matplotlib.pyplot as plt

baseline = np.load("baseline.npy", allow_pickle='TRUE').item()
case1 = np.load("case1.npy", allow_pickle='TRUE').item()
case2 = np.load("case2.npy", allow_pickle='TRUE').item()
case3 = np.load("case3.npy", allow_pickle='TRUE').item()
case4 = np.load("case4.npy", allow_pickle='TRUE').item()
case5 = np.load("case5.npy", allow_pickle='TRUE').item()

def mean_noise(data):
    for key in data.keys():
        if len(data[key]) == 0:
            data[key] = 0.0
        else:
            data[key] = np.round(np.mean(data[key]), 2)
    if len(data.keys()) == 34:
        data['35'] = 0.0
        
mean_noise(baseline)
mean_noise(case1)
mean_noise(case2)
mean_noise(case3)
mean_noise(case4)
mean_noise(case5)

baseline = dict(sorted(baseline.items()))
case1 = dict(sorted(case1.items()))
case2 = dict(sorted(case2.items()))
case3 = dict(sorted(case3.items()))
case4 = dict(sorted(case4.items()))
case5 = dict(sorted(case5.items()))

plt.figure()
plt.scatter(baseline.keys(), baseline.values(), label="baseline")
plt.scatter(case1.keys(), case1.values(), label="case1")
plt.scatter(case2.keys(), case2.values(), label="case2")
plt.scatter(case3.keys(), case3.values(), label="case3")
plt.scatter(case4.keys(), case4.values(), label="case4")
plt.scatter(case5.keys(), case5.values(), label="case5")
plt.suptitle("Noise Immission")
plt.xlabel("Time")
plt.ylabel("Noise Level")
plt.legend()
plt.show()
