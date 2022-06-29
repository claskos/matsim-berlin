#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Jun 24 22:19:48 2022

@author: Christos
"""

import json
import pandas as pd
import numpy as np
from collections import Counter
import matplotlib.pyplot as plt
import seaborn as sns


def extract_data(json_file, trips_file):
    f = open(json_file, 'r')
    #f = open("affected_persons_hw.json", 'r')
    persons = json.load(f)
    f.close()
    persons = persons["persons"]
    
    print("Affected Persons: %d" % len(persons))
    
    trips = pd.read_csv(trips_file, sep=";", dtype=str)
    #trips = pd.read_csv("trips_hw.csv", sep=";", dtype=str)
    #print(trips.iloc[[0]])
    
    ids = []
    home_in_block = []
    for p in persons:
        ids.append(p["id"])
        if 'home' in p["activities"]:
            home_in_block.append(p["id"])
    #print(len(home_in_block))
        
    trips = trips[trips.person.isin(ids)]
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
    
    activities = []
    home_region = []

    for p in persons:
        home_region.append(p['home_region'])
        for act in p['activities']:
            activities.append(act)
    
    return travel_time_dict, travel_dist_dict, pt_wait_time, mode_share, activities, home_region, trips, home_in_block

def plotTravelTimes(time_base, time_hw):
    #### travel time
    plt.figure()
    data = [np.ravel(np.fromiter(time_base.values(), dtype=float)), np.ravel(np.fromiter(time_hw.values(), dtype=float))]
    boxplot = sns.boxplot(data = data, showfliers=False)
    medians = np.round(np.asarray([np.median(data[0]), np.median(data[1])]), 2)
    vertical_offset = np.median(medians) * 0.05 # offset from median for display
    for xtick in boxplot.get_xticks(): # display median on plot as number
        boxplot.text(xtick,medians[xtick] + vertical_offset,medians[xtick], 
                horizontalalignment='center',size='x-small',color='w',weight='semibold')
    plt.title("Travel Times")
    plt.xlabel("Scenario")
    plt.ylabel("Travel Time (minutes)")
    plt.xticks(ticks=[0,1], labels=["Baseline", "Homework"])
    plt.show()
    
def plotTravelDist(dist_base, dist_hw):
    #### travel distance
    plt.figure()
    data = [np.ravel(np.fromiter(dist_base.values(), dtype=float)), np.ravel(np.fromiter(dist_hw.values(), dtype=float))]
    boxplot = sns.boxplot(data = data, showfliers=False)
    medians = np.round(np.asarray([np.median(data[0]), np.median(data[1])]), 2)
    vertical_offset = np.median(medians) * 0.05 # offset from median for display
    for xtick in boxplot.get_xticks(): # display median on plot as number
        boxplot.text(xtick,medians[xtick] + vertical_offset,medians[xtick], 
                horizontalalignment='center',size='x-small',color='w',weight='semibold')
    plt.title("Travel Distance")
    plt.xlabel("Scenario")
    plt.ylabel("Travel Distance (km)")
    plt.xticks(ticks=[0,1], labels=["Baseline", "Homework"])
    plt.show()
    
def plotPtWait(wait_base, wait_hw):
    #### pt wait times
    plt.figure()
    data = [np.ravel(wait_base), np.ravel(wait_hw)]
    boxplot = sns.boxplot(data = data, showfliers=False)
    medians = np.round(np.asarray([np.median(data[0]), np.median(data[1])]), 2)
    vertical_offset = np.median(medians) * 0.05 # offset from median for display
    for xtick in boxplot.get_xticks(): # display median on plot as number
        boxplot.text(xtick,medians[xtick] + vertical_offset,medians[xtick], 
                horizontalalignment='center',size='x-small',color='w',weight='semibold')
    plt.suptitle("PT Wait Times")
    plt.title("Number of PT Uses: Base: " + str(len(wait_base)) + " Hw: " + str(len(wait_hw)))
    plt.xlabel("Scenario")
    plt.ylabel("Wait Time (minutes)")
    plt.xticks(ticks=[0,1], labels=["Baseline", "Homework"])
    plt.show()
    
def plotModeShare(mode_base, mode_hw):
    #### mode share
    len_base = len(mode_base)
    len_hw = len(mode_hw)
    mode_base = Counter(mode_base)
    mode_hw = Counter(mode_hw)
    fig, (ax1, ax2) = plt.subplots(1, 2, sharey=True)
    ax1.bar(np.arange(len(mode_base)), np.fromiter(mode_base.values(), dtype=int) / len_base)
    ax1.set(xticks=np.arange(len(mode_base)), xticklabels=list(mode_base.keys()))

    ax2.bar(np.arange(len(mode_hw)), np.fromiter(mode_hw.values(), dtype=int) / len_hw, color="orange")
    ax2.set(xticks=np.arange(len(mode_hw)), xticklabels=list(mode_hw.keys()))
    
    fig.suptitle("Modeshare of Affected Persons")
    ax1.set(xlabel="Mode Baseline")
    ax2.set(xlabel="Mode Homework")
    ax1.set(ylabel="Share")
    plt.show()
    
def plotActivities(act_base, act_hw):
    #### activities
    act_base_len = len(act_base)
    act_base = Counter(act_base)
    act_hw_len = len(act_hw)
    act_hw = Counter(act_hw)

    fig, (ax1, ax2) = plt.subplots(1, 2, sharey=True)
    ax1.bar(np.arange(len(act_base)), np.fromiter(act_base.values(), dtype=int) / act_base_len)
    ax1.set(xticks=np.arange(len(act_base)), xticklabels=list(act_base.keys()))
    
    ax2.bar(np.arange(len(act_hw)), np.fromiter(act_hw.values(), dtype=int) / act_hw_len, color="orange")
    ax2.set(xticks=np.arange(len(act_hw)), xticklabels=list(act_hw.keys()))
    fig.suptitle("Activities of Affected Persons")
    ax1.set(xlabel="Activity Baseline")
    ax2.set(xlabel="Activity Homework")
    ax1.set(ylabel="Share")
    plt.show()
    
def plotHomeRegion(home_base, home_hw):
    #### home region
    home_base_len = len(home_base)
    home_base = Counter(home_base)
    home_hw_len = len(home_hw)
    home_hw = Counter(home_hw)

    fig, (ax1, ax2) = plt.subplots(1, 2, sharey=True)
    ax1.bar(np.arange(len(home_base)), np.fromiter(home_base.values(), dtype=int) / home_base_len)
    ax1.set(xticks=np.arange(len(home_base)), xticklabels=list(home_base.keys()))
    
    ax2.bar(np.arange(len(home_hw)), np.fromiter(home_hw.values(), dtype=int) / home_hw_len, color="orange")
    ax2.set(xticks=np.arange(len(home_hw)), xticklabels=list(home_hw.keys()))
    fig.suptitle("Home Regions of Affected Persons")
    ax1.set(xlabel="Region Baseline")
    ax2.set(xlabel="Region Homework")
    ax1.set(ylabel="Share")
    plt.show()
    
def plotTimePerKm(time_base, dist_base, time_hw, dist_hw):
    base_time_per_km = []
    for key in time_base:
        curr_time = time_base.get(key)
        curr_dist = dist_base.get(key)
        base_time_per_km.append(curr_time / curr_dist)
    
    hw_time_per_km = []
    for key in time_hw:
        curr_time = time_hw.get(key)
        curr_dist = dist_hw.get(key)
        hw_time_per_km.append(curr_time / curr_dist)
        
    #### minutes per km
    plt.figure()
    data = [np.ravel(base_time_per_km), np.ravel(hw_time_per_km)]
    boxplot = sns.boxplot(data = data, showfliers=False)
    medians = np.round(np.asarray([np.median(data[0]), np.median(data[1])]), 2)
    vertical_offset = np.median(medians) * 0.05 # offset from median for display
    for xtick in boxplot.get_xticks(): # display median on plot as number
        boxplot.text(xtick,medians[xtick] + vertical_offset,medians[xtick], 
                horizontalalignment='center',size='x-small',color='w',weight='semibold')
    plt.suptitle("Minutes per Km of Travel")
    plt.xlabel("Scenario")
    plt.ylabel("Time per Km (minutes)")
    plt.xticks(ticks=[0,1], labels=["Baseline", "Homework"])
    plt.show()
    
def plotTimePerKmCarOnly(trips_base, trips_hw):
    #print("F")
    hw_using_car = list()
    for row in trips_hw: # select legs with car and person does not exists yet in list
        if row[3] == 'car' and not row[0] in hw_using_car:
            hw_using_car.append(row[0])
    
    base_time_per_dist = []
    hw_time_per_dist = []
    for car_user in hw_using_car: # get trips of that person in both scenarios
        base_index = np.where(trips_base[:, 0] == car_user)
        hw_index = np.where(trips_hw[:, 0] == car_user)
        
        # calculate travel time/km for baseline and hw scenario
        base_time = np.sum(trips_base[base_index][:, 1])
        base_dist = np.sum(trips_base[base_index][:, 2])
        base_time_per_dist.append(base_time / base_dist)
        
        hw_time = np.sum(trips_hw[hw_index][:, 1])
        hw_dist = np.sum(trips_hw[hw_index][:, 2])
        hw_time_per_dist.append(hw_time / hw_dist)

    #### minutes per km car users only
    plt.figure()
    data = [base_time_per_dist, hw_time_per_dist]
    boxplot = sns.boxplot(data = data, showfliers=False)
    medians = np.round(np.asarray([np.median(data[0]), np.median(data[1])]), 2)
    vertical_offset = np.median(medians) * 0.05 # offset from median for display
    for xtick in boxplot.get_xticks(): # display median on plot as number
        boxplot.text(xtick,medians[xtick] + vertical_offset,medians[xtick], 
                horizontalalignment='center',size='x-small',color='w',weight='semibold')
    plt.suptitle("Minutes per Km of Travel for Car Users in Hw Scenario")
    plt.xlabel("Scenario")
    plt.ylabel("Time per Km (minutes)")
    plt.xticks(ticks=[0,1], labels=["Baseline", "Homework"])
    plt.show()
    
def plotHomeInBlockModeChange(trips_base, trips_hw, home_in_block_base, home_in_block_hw):
    #### plot only mode share of persons actually living inside the mega block
    modes_base = []
    for i in range(len(trips_base)):
        if trips_base[i, 0] in home_in_block_base:
            modes_base.append(trips_base[i, 3])
    
    modes_hw = []
    for i in range(len(trips_hw)):
        if trips_hw[i, 0] in home_in_block_hw:
            modes_hw.append(trips_hw[i, 3])
            
    len_base = len(modes_base)
    len_hw = len(modes_hw)
    modes_base = Counter(modes_base)
    modes_hw = Counter(modes_hw)
    fig, (ax1, ax2) = plt.subplots(1, 2, sharey=True)
    ax1.bar(np.arange(len(modes_base)), np.fromiter(modes_base.values(), dtype=int) / len_base)
    ax1.set(xticks=np.arange(len(modes_base)), xticklabels=list(modes_base.keys()))

    ax2.bar(np.arange(len(modes_hw)), np.fromiter(modes_hw.values(), dtype=int) / len_hw, color="orange")
    ax2.set(xticks=np.arange(len(modes_hw)), xticklabels=list(modes_hw.keys()))
    
    fig.suptitle("Modeshare of Persons Living in Mega Block")
    ax1.set(xlabel="Mode Baseline")
    ax2.set(xlabel="Mode Homework")
    ax1.set(ylabel="Share")
    plt.show()

travel_time_base, travel_dist_base, pt_wait_base, modes_base, act_base, home_base, trips_base, home_in_block_base = extract_data("affected_persons_baseline.json", "trips_baseline.csv")
travel_time_hw, travel_dist_hw, pt_wait_hw, modes_hw, act_hw, home_hw, trips_hw, home_in_block_hw = extract_data("affected_persons_hw.json", "trips_hw.csv")

sns.set_theme(style="whitegrid")

plotTravelTimes(travel_time_base, travel_time_hw)
plotTravelDist(travel_dist_base, travel_dist_hw)
plotPtWait(pt_wait_base, pt_wait_hw)
plotModeShare(modes_base, modes_hw)
plotActivities(act_base, act_hw)
plotHomeRegion(home_base, home_hw)
plotTimePerKm(travel_time_base, travel_dist_base, travel_time_hw, travel_dist_hw)
plotTimePerKmCarOnly(trips_base, trips_hw)
plotHomeInBlockModeChange(trips_base, trips_hw, home_in_block_base, home_in_block_hw)