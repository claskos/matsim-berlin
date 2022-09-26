import glob
import pandas as pd
import numpy as np


files = glob.glob("*.csv")

noise = dict()

for f in files:
    data = pd.read_csv(f, sep=";", dtype=str)
    time = data.columns[5].split(" ")[2].split(":")[0]
    noise[time] = data.iloc[:, 5].to_numpy(dtype=float)

np.save("case5.npy", noise)
