#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Jun 24 22:19:48 2022

@author: Christos
"""

import pandas as pd
import numpy as np
from collections import Counter
import matplotlib.pyplot as plt
import seaborn as sns


def extract_data(trips_file):
    
    trips = pd.read_csv(trips_file, sep=";", dtype=str)
    
    trips = trips[["person", "trav_time", "traveled_distance", "main_mode", "wait_time"]].to_numpy()
    #print(len(trips))
    travel_time_dict = dict()
    travel_dist_dict = dict()
    pt_wait_time = []
    
    # change times to minutes and store them in their dict per person, also store distance and convert and store pt wait time
    for i in range(len(trips)):
        time = trips[i, 1].split(':')
        trips[i, 1] = (float(time[0]) * 3600 + float(time[1]) * 60 + float(time[2])) / 60 # time in minutes
        trips[i, 2] = int(trips[i, 2]) / 1000 #  in km
        if not travel_time_dict.get(trips[i, 0]):
            travel_time_dict[trips[i, 0]] = 0
        travel_time_dict[trips[i, 0]] += trips[i, 1]
        
        if not travel_dist_dict.get(trips[i, 0]):
            travel_dist_dict[trips[i, 0]] = 0
        travel_dist_dict[trips[i, 0]] += trips[i, 2]
        
        wait_time = trips[i, 4].split(':')
        trips[i, 4] = (float(wait_time[0]) * 3600 + float(wait_time[1]) * 60 + float(wait_time[2])) / 60 # time in minutes
        if trips[i, 3] == 'pt':
            pt_wait_time.append(trips[i, 4])
            
        
    mode_share = trips[:,3]
    #print(travel_time_dict)
    #print(travel_dist_dict)
    
    trips_pd = pd.DataFrame({'person': trips[:, 0], 'trav_time': trips[:, 1],
                             'trav_distance':trips[:, 2], 'mode':trips[:, 3],
                             'wait_time':trips[:, 4]})
    # new_path = trips_file.split('.')[0] + "_new.csv"
    # trips_pd.to_csv(new_path, sep=";")
    
    return travel_time_dict, travel_dist_dict, pt_wait_time, mode_share, trips_pd

def plotTravelTimes(time_base, time_case1, time_case2, time_case3, time_case4, time_case5):
    #### travel time
    plt.figure()
    data = [np.ravel(np.fromiter(time_base.values(), dtype=float)), np.ravel(np.fromiter(time_case1.values(), dtype=float)),
            np.ravel(np.fromiter(time_case2.values(), dtype=float)), np.ravel(np.fromiter(time_case3.values(), dtype=float)),
            np.ravel(np.fromiter(time_case4.values(), dtype=float)), np.ravel(np.fromiter(time_case5.values(), dtype=float))]
    boxplot = sns.boxplot(data = data, showfliers=False)
    medians = np.round(np.asarray([np.median(data[0]), np.median(data[1]), np.median(data[2]), np.median(data[3]), np.median(data[4]), np.median(data[5])]), 2)
    vertical_offset = np.median(medians) * 0.05 # offset from median for display
    for xtick in boxplot.get_xticks(): # display median on plot as number
        boxplot.text(xtick,medians[xtick] + vertical_offset,medians[xtick], 
                horizontalalignment='center',size='x-small',color='w',weight='semibold')
    plt.suptitle("Travel Times")
    plt.xlabel("Scenario")
    plt.ylabel("Travel Time (minutes)")
    plt.xticks(ticks=[0,1,2,3,4,5], labels=["Baseline", "Case 1", "Case 2", "Case 3", "Case 4", "Case 5"])
    plt.show()
    
def plotTravelDist(dist_base, dist_case1, dist_case2, dist_case3, dist_case4, dist_case5):
    #### travel distance
    plt.figure()
    data = [np.ravel(np.fromiter(dist_base.values(), dtype=float)), np.ravel(np.fromiter(dist_case1.values(), dtype=float)),
            np.ravel(np.fromiter(dist_case2.values(), dtype=float)), np.ravel(np.fromiter(dist_case3.values(), dtype=float)),
            np.ravel(np.fromiter(dist_case4.values(), dtype=float)), np.ravel(np.fromiter(dist_case5.values(), dtype=float))]
    boxplot = sns.boxplot(data = data, showfliers=False)
    medians = np.round(np.asarray([np.median(data[0]), np.median(data[1]), np.median(data[2]), np.median(data[3]), np.median(data[4]), np.median(data[5])]), 2)
    vertical_offset = np.median(medians) * 0.05 # offset from median for display
    for xtick in boxplot.get_xticks(): # display median on plot as number
        boxplot.text(xtick,medians[xtick] + vertical_offset,medians[xtick], 
                horizontalalignment='center',size='x-small',color='w',weight='semibold')
    plt.suptitle("Travel Distance")
    plt.xlabel("Scenario")
    plt.ylabel("Travel Distance (km)")
    plt.xticks(ticks=[0,1,2,3,4,5], labels=["Baseline", "Case 1", "Case 2", "Case 3", "Case 4", "Case 5"])
    plt.show()
    
def plotPtWait(wait_base, wait_case1, wait_case2, wait_case3, wait_case4, wait_case5):
    #### pt wait times
    plt.figure()
    data = [np.ravel(wait_base), np.ravel(wait_case1), np.ravel(wait_case2), np.ravel(wait_case3), np.ravel(wait_case4), np.ravel(wait_case5)]
    boxplot = sns.boxplot(data = data, showfliers=False)
    medians = np.round(np.asarray([np.median(data[0]), np.median(data[1]), np.median(data[2]), np.median(data[3]), np.median(data[4]), np.median(data[5])]), 2)
    vertical_offset = np.median(medians) * 0.05 # offset from median for display
    for xtick in boxplot.get_xticks(): # display median on plot as number
        boxplot.text(xtick,medians[xtick] + vertical_offset,medians[xtick], 
                horizontalalignment='center',size='x-small',color='w',weight='semibold')
        boxplot.text(xtick,medians[xtick] - vertical_offset * 10, "PT Uses: " + str(len(data[xtick])),
                horizontalalignment='center',size='x-small',color='w',weight='semibold')
    plt.suptitle("PT Wait Times")
    plt.xlabel("Scenario")
    plt.ylabel("Wait Time (minutes)")
    plt.xticks(ticks=[0,1,2,3,4,5], labels=["Baseline", "Case 1", "Case 2", "Case 3", "Case 4", "Case 5"])
    plt.show()
    
def plotModeShare(mode_base, mode_case1, mode_case2, mode_case3, mode_case4, mode_case5):
    #### mode share
    len_base = len(mode_base)
    len_case1 = len(mode_case1)
    len_case2 = len(mode_case2)
    len_case3 = len(mode_case3)
    len_case4 = len(mode_case4)
    len_case5 = len(mode_case5)
    
    mode_base = Counter(mode_base)
    mode_case1 = Counter(mode_case1)
    mode_case2 = Counter(mode_case2)
    mode_case3 = Counter(mode_case3)
    mode_case4 = Counter(mode_case4)
    mode_case5 = Counter(mode_case5)
    
    fig, (ax1, ax2, ax3, ax4, ax5, ax6) = plt.subplots(1, 6, sharey=True)
    ax1.bar(np.arange(len(mode_base)), np.fromiter(mode_base.values(), dtype=int) / len_base)
    ax1.set_xticks(np.arange(len(mode_base)))
    ax1.set_xticklabels(list(mode_base.keys()), rotation=45, ha='right')
    #ax1.set(xticks=np.arange(len(mode_base)), xticklabels=list(mode_base.keys()))

    ax2.bar(np.arange(len(mode_case1)), np.fromiter(mode_case1.values(), dtype=int) / len_case1, color="orange")
    ax2.set_xticks(np.arange(len(mode_case1)))
    ax2.set_xticklabels(list(mode_case1.keys()), rotation=45, ha='right')
    #ax2.set(xticks=np.arange(len(mode_case1)), xticklabels=list(mode_case1.keys()))
    
    ax3.bar(np.arange(len(mode_case2)), np.fromiter(mode_case2.values(), dtype=int) / len_case2, color="green")
    ax3.set_xticks(np.arange(len(mode_case2)))
    ax3.set_xticklabels(list(mode_case2.keys()), rotation=45, ha='right')
    #ax3.set(xticks=np.arange(len(mode_case2)), xticklabels=list(mode_case2.keys()))
    
    ax4.bar(np.arange(len(mode_case3)), np.fromiter(mode_case3.values(), dtype=int) / len_case3, color="red")
    ax4.set_xticks(np.arange(len(mode_case3)))
    ax4.set_xticklabels(list(mode_case3.keys()), rotation=45, ha='right')
    #ax4.set(xticks=np.arange(len(mode_case3)), xticklabels=list(mode_case3.keys()))
    
    ax5.bar(np.arange(len(mode_case4)), np.fromiter(mode_case4.values(), dtype=int) / len_case4, color="purple")
    ax5.set_xticks(np.arange(len(mode_case4)))
    ax5.set_xticklabels(list(mode_case4.keys()), rotation=45, ha='right')
    #ax5.set(xticks=np.arange(len(mode_case4)), xticklabels=list(mode_case4.keys()))
    
    ax6.bar(np.arange(len(mode_case5)), np.fromiter(mode_case5.values(), dtype=int) / len_case5, color="brown")
    ax6.set_xticks(np.arange(len(mode_case5)))
    ax6.set_xticklabels(list(mode_case5.keys()), rotation=45, ha='right')
    #ax6.set(xticks=np.arange(len(mode_case5)), xticklabels=list(mode_case5.keys()))
    
    fig.suptitle("Modeshare")
    ax1.set(xlabel="Mode Baseline")
    ax2.set(xlabel="Mode Case 1")
    ax3.set(xlabel="Mode Case 2")
    ax4.set(xlabel="Mode Case 3")
    ax5.set(xlabel="Mode Case 4")
    ax6.set(xlabel="Mode Case 5")
    ax1.set(ylabel="Share")
    plt.show()
    
    
def plotTimePerKm(time_base, dist_base, time_case1, dist_case1, time_case2, dist_case2, time_case3, dist_case3, time_case4, dist_case4, time_case5, dist_case5):
    base_time_per_km = []
    for key in time_base:
        curr_time = time_base.get(key)
        curr_dist = dist_base.get(key)
        base_time_per_km.append(curr_time / curr_dist)
    
    case1_time_per_km = []
    for key in time_case1:
        curr_time = time_case1.get(key)
        curr_dist = dist_case1.get(key)
        case1_time_per_km.append(curr_time / curr_dist)
        
    case2_time_per_km = []
    for key in time_case2:
        curr_time = time_case2.get(key)
        curr_dist = dist_case2.get(key)
        case2_time_per_km.append(curr_time / curr_dist)
    
    case3_time_per_km = []
    for key in time_case3:
        curr_time = time_case3.get(key)
        curr_dist = dist_case3.get(key)
        case3_time_per_km.append(curr_time / curr_dist)
        
    case4_time_per_km = []
    for key in time_case4:
        curr_time = time_case4.get(key)
        curr_dist = dist_case4.get(key)
        case4_time_per_km.append(curr_time / curr_dist)
        
    case5_time_per_km = []
    for key in time_case5:
        curr_time = time_case5.get(key)
        curr_dist = dist_case5.get(key)
        case5_time_per_km.append(curr_time / curr_dist)
        
    #### minutes per km
    plt.figure()
    data = [np.ravel(base_time_per_km), np.ravel(case1_time_per_km), np.ravel(case2_time_per_km), np.ravel(case3_time_per_km), np.ravel(case4_time_per_km), np.ravel(case5_time_per_km)]
    boxplot = sns.boxplot(data = data, showfliers=False)
    medians = np.round(np.asarray([np.median(data[0]), np.median(data[1]), np.median(data[2]), np.median(data[3]), np.median(data[4]), np.median(data[5])]), 2)
    vertical_offset = np.median(medians) * 0.05 # offset from median for display
    for xtick in boxplot.get_xticks(): # display median on plot as number
        boxplot.text(xtick,medians[xtick] + vertical_offset,medians[xtick], 
                horizontalalignment='center',size='x-small',color='w',weight='semibold')
    plt.suptitle("Minutes per Km of Travel")
    plt.xlabel("Scenario")
    plt.ylabel("Time per Km (minutes)")
    plt.xticks(ticks=[0,1,2,3,4,5], labels=["Baseline", "Case 1", "Case 2", "Case 3", "Case 4", "Case 5"])
    plt.show()
    
def plotTimePerKmCarOnly(trips_base, trips_case1, trips_case2, trips_case3, trips_case4, trips_case5):
    #print("F")
    car_only_base = trips_base[trips_base['mode'] == 'car']
    car_only_case1 = trips_case1[trips_case1['mode'] == 'car']
    car_only_case2 = trips_case2[trips_case2['mode'] == 'car']
    car_only_case3 = trips_case3[trips_case3['mode'] == 'car']
    car_only_case4 = trips_case4[trips_case4['mode'] == 'car']
    car_only_case5 = trips_case5[trips_case5['mode'] == 'car']
    
    car_only_base = car_only_base.assign(time_per_km = car_only_base['trav_time'] / car_only_base['trav_distance'])
    car_only_case1 = car_only_case1.assign(time_per_km = car_only_case1['trav_time'] / car_only_case1['trav_distance'])
    car_only_case2 = car_only_case2.assign(time_per_km = car_only_case2['trav_time'] / car_only_case2['trav_distance'])
    car_only_case3 = car_only_case3.assign(time_per_km = car_only_case3['trav_time'] / car_only_case3['trav_distance'])
    car_only_case4 = car_only_case4.assign(time_per_km = car_only_case4['trav_time'] / car_only_case4['trav_distance'])
    car_only_case5 = car_only_case5.assign(time_per_km = car_only_case5['trav_time'] / car_only_case5['trav_distance'])
    
    time_per_km_base = car_only_base['time_per_km'].to_numpy()
    time_per_km_case1 = car_only_case1['time_per_km'].to_numpy()
    time_per_km_case2 = car_only_case2['time_per_km'].to_numpy()
    time_per_km_case3 = car_only_case3['time_per_km'].to_numpy()
    time_per_km_case4 = car_only_case4['time_per_km'].to_numpy()
    time_per_km_case5 = car_only_case5['time_per_km'].to_numpy()

    #### minutes per km car users only
    plt.figure()
    data = [time_per_km_base, time_per_km_case1, time_per_km_case2, time_per_km_case3, time_per_km_case4, time_per_km_case5]
    boxplot = sns.boxplot(data = data, showfliers=False)
    medians = np.round(np.asarray([np.median(data[0]), np.median(data[1]), np.median(data[2]), np.median(data[3]), np.median(data[4]), np.median(data[5])]), 2)
    vertical_offset = np.median(medians) * 0.05 # offset from median for display
    for xtick in boxplot.get_xticks(): # display median on plot as number
        boxplot.text(xtick,medians[xtick] + vertical_offset,medians[xtick], 
                horizontalalignment='center',size='x-small',color='w',weight='semibold')
        boxplot.text(xtick,medians[xtick] - vertical_offset * 3, "Trips: " + str(len(data[xtick])),
                horizontalalignment='center',size='x-small',color='w',weight='semibold')
    plt.suptitle("Minutes per Km for Car Users")
    plt.xlabel("Scenario")
    plt.ylabel("Time per Km (minutes)")
    plt.xticks(ticks=[0,1,2,3,4,5], labels=["Baseline", "Case 1", "Case 2", "Case 3", "Case 4", "Case 5"])
    plt.show()
    

travel_time_base, travel_dist_base, pt_wait_base, modes_base, trips_base = extract_data("trips_baseline.csv")
travel_time_case1, travel_dist_case1, pt_wait_case1, modes_case1, trips_case1 = extract_data("trips_case1.csv")
travel_time_case2, travel_dist_case2, pt_wait_case2, modes_case2, trips_case2 = extract_data("trips_case2.csv")
travel_time_case3, travel_dist_case3, pt_wait_case3, modes_case3, trips_case3 = extract_data("trips_case3.csv")
travel_time_case4, travel_dist_case4, pt_wait_case4, modes_case4, trips_case4 = extract_data("trips_case4.csv")
travel_time_case5, travel_dist_case5, pt_wait_case5, modes_case5, trips_case5 = extract_data("trips_case5.csv")

sns.set_theme(style="whitegrid")

plotTravelTimes(travel_time_base, travel_time_case1, travel_time_case2, travel_time_case3, travel_time_case4, travel_time_case5)
plotTravelDist(travel_dist_base, travel_dist_case1, travel_dist_case2, travel_dist_case3, travel_dist_case4, travel_dist_case5)
plotPtWait(pt_wait_base, pt_wait_case1, pt_wait_case2, pt_wait_case3, pt_wait_case4, pt_wait_case5)
plotModeShare(modes_base, modes_case1, modes_case2, modes_case3, modes_case4, modes_case5)
plotTimePerKm(travel_time_base, travel_dist_base, travel_time_case1, travel_dist_case1, travel_time_case2, travel_dist_case2, travel_time_case3, travel_dist_case3, travel_time_case4, travel_dist_case4, travel_time_case5, travel_dist_case5)
plotTimePerKmCarOnly(trips_base, trips_case1, trips_case2, trips_case3, trips_case4, trips_case5)