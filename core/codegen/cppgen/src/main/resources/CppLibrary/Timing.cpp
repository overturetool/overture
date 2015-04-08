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


Timing* Timing::instance_ = NULL;



TimedFunction::TimedFunction()
{
	start_measurements.reserve(60000);
	end_measurements.reserve(60000);
};

void TimedFunction::add_start(const Time& t)
	{
		start_measurements.push_back(t);
	};

void TimedFunction::add_end(const Time& t)
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
		double dif =  std::chrono::duration_cast<std::chrono::nanoseconds>(*b - *a).count();

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
	//clock_gettime(CLOCK_PROCESS_CPUTIME_ID,&t);

	// put in list at index
	timing_list[uid].add_start(TimeType::now());

}

void Timing::log_end(const uint32_t uid)
{
	timing_list[uid].add_end(TimeType::now());
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
		if((n = names_map.find(i)) != names_map.end() && it->get_number_of_measurements() > 0)
		{
			std::stringstream ss;

			ss << filename_prefix << n->first << ".tim";
			std::string fname =  ss.str();
			std::cout << fname << std::endl;
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

