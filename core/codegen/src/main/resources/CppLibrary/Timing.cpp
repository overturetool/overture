/*
 * Timing.cpp
 *
 *  Created on: Feb 10, 2015
 *      Author: morten
 */

#include "Timing.h"

#ifndef TIMING_N_METHODS
#define TIMING_N_METHODS 1000
#endif
namespace timing {

timespec diff(const timespec& start,const timespec& end)
{
	timespec temp;
	if ((end.tv_nsec-start.tv_nsec)<0) {
		temp.tv_sec = end.tv_sec-start.tv_sec-1;
		temp.tv_nsec = 1000000000+end.tv_nsec-start.tv_nsec;
	} else {
		temp.tv_sec = end.tv_sec-start.tv_sec;
		temp.tv_nsec = end.tv_nsec-start.tv_nsec;
	}
	return temp;
}

double to_secs(const timespec& tim)
{
	return tim.tv_sec + tim.tv_nsec / 1E9;
}

double to_nsecs(const timespec& tim)
{
	return tim.tv_sec * 1E9 + tim.tv_nsec;
}


Timing* Timing::instance_ = NULL;



TimedFunction::TimedFunction()
{
	start_measurements.reserve(10000);
	end_measurements.reserve(10000);
};

void TimedFunction::add_start(const timespec& t)
	{
		start_measurements.push_back(t);
	};

void TimedFunction::add_end(const timespec& t)
{
	end_measurements.push_back(t);
}

double TimedFunction::get_number_of_measurements()
{
	return start_measurements.size();
}

std::vector<double> TimedFunction::get_measurements()
{
	std::vector<double> out;
	container_t::iterator a = start_measurements.begin();
	container_t::iterator b = end_measurements.begin();

	for(; (a != start_measurements.end()); ++a)
	{
		double dif = to_nsecs(diff(*a,*b));
		out.push_back(dif);
		++b;
	}
	return out;
}



Timing::Timing(const unsigned int e_elems) : timing_list(e_elems)
{

}

Timing* Timing::get_instance()
{
	if(instance_ == NULL)
	{
		instance_ = new Timing(TIMING_N_METHODS);

	}
	return instance_;
}

void Timing::log_start(const uint32_t uid)
{
	// get time
	clock_gettime(CLOCK_PROCESS_CPUTIME_ID,&t);
	// put in list at index
	timing_list[uid].add_start(t);

}

void Timing::log_end(const uint32_t uid)
{
	clock_gettime(CLOCK_PROCESS_CPUTIME_ID,&t);
	timing_list[uid].add_end(t);
}

measurements_t Timing::get_measurements()
{
	return timing_list;
}

void Timing::to_file(const std::string& filename_prefix)
{

	uint32_t i = 0;
	for(std::vector<TimedFunction>::iterator it = timing_list.begin(); it != timing_list.end();++it)
	{
		std::map<uint32_t,std::string>::iterator n;
		if((n = names_map.find(i)) != names_map.end())
		{
			std::string fname =  filename_prefix + n->second + ".tim";
			std::ofstream fd;

			fd.open(fname.c_str());
			std::vector<double> meas = it->get_measurements();
			for(std::vector<double>::iterator m = meas.begin();m != meas.end();++m)
			{
				fd << *m << std::endl;
			}
			fd.close();
		}
		i++;
	}
}

void Timing::set_name_for(const uint32_t uid,const std::string& name)
{
	names_map.insert(std::make_pair(uid,name));
}

std::string Timing::get_name(uint32_t uid)
{
	std::map<uint32_t,std::string>::iterator name;
	name = names_map.find(uid);
	if(name == names_map.end())
	{
		return "";
	}
	else
	{
		return name->second;
	}
}





} /* namespace timing */
