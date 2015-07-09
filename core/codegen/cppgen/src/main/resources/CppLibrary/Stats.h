/*
 * Stats.h
 *
 *  Created on: Feb 10, 2015
 *      Author: morten
 */

#ifndef STATS_H_
#define STATS_H_
#include <vector>
#include <math.h>
#include <algorithm>
#include "Timing.h"
namespace timing {
namespace stats {


struct Stats
{
	double mean;
	double median;
	double min;
	double max;
	double stddev;
};


Stats calculate_statistics(const std::vector<double>& measurements);

void print_stats(measurements_t& meas);

} /* namespace stats */
} /* namespace timing */

#endif /* STATS_H_ */
