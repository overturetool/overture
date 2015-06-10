/*
 * Stats.cpp
 *
 *  Created on: Feb 10, 2015
 *      Author: morten
 */

#include "Stats.h"

namespace timing {
namespace stats {

Stats calculate_statistics(const std::vector<double>& measurements)
{
	Stats s;

	s.min = s.max = s.median = s.mean = s.stddev = 0.0;

	if(measurements.size() == 0)
	{
		return s;
	}

	double sum = 0.0;
	double var = 0.0;

	for(std::vector<double>::const_iterator it = measurements.begin(); it != measurements.end();++it)
	{
		sum += *it;
	}
	s.mean = sum/measurements.size();


	for(std::vector<double>::const_iterator it = measurements.begin(); it != measurements.end();++it)
	{
		var += pow(s.mean - *it,2);
	}
	s.stddev = sqrt(var/measurements.size());

	/* meadian*/

	std::vector<double> sorted;
	std::copy(measurements.begin(),measurements.end(),std::back_inserter(sorted));
	std::sort(sorted.begin(),sorted.end());

	if(sorted.size() > 1)
	{
		s.median = sorted[sorted.size()/2];
		s.min = sorted[0];
		s.max = sorted[sorted.size()-1];
	}
	else if(sorted.size() > 0)
	{
		s.median = s.min = s.max = sorted[0];
	}


	return s;
}

void print_stats( measurements_t& meas)
{
	uint32_t uid = 0;
	std::cout << "Statistics: " << std::endl;
	for(timing::measurements_t::iterator a = meas.begin();a!= meas.end();++a)
	{
		std::string name = timing::Timing::get_instance()->get_name(uid);
		if(!name.empty())
		{

			std::vector<double> mm = a->get_measurements();
			if(mm.size()> 0)
			{
				std::cout << "Function: " << name << std::endl;
				timing::stats::Stats s = timing::stats::calculate_statistics(mm);
				std::cout << "N Measurements: " << mm.size() << std::endl;
				std::cout << "Mean: " << s.mean << std::endl;
				std::cout << "Median: " << s.median << std::endl;
				std::cout << "Min: " << s.min << std::endl;
				std::cout << "Max: " << s.max << std::endl;
				std::cout << "Stddev: " << s.stddev << std::endl;
			}

		}
		uid++;
	}
}

} /* namespace stats */
} /* namespace timing */
