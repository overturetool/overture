/*
 * Timing.h
 *
 *  Created on: Feb 10, 2015
 *      Author: morten
 */

#ifndef TIMING_H_
#define TIMING_H_


#include <iostream>
#include <stdint.h>
#include <vector>
#include <list>
#include <map>
#include <math.h>
#include <fstream>
#include <sched.h>
#include <chrono>
#include <sstream>


namespace timing {


class TimedFunction;
class Timing;
class TimedScope;

typedef std::vector<TimedFunction> measurements_t;

typedef std::chrono::time_point<std::chrono::steady_clock> Time;
typedef std::chrono::steady_clock TimeType;

class TimedFunction
{
public:

	typedef std::vector<Time> container_t;

	TimedFunction();


	void add_start(const Time& t);
	void add_end(const Time& t);

	double get_number_of_measurements();

	std::vector<double> get_measurements();

private:
	container_t start_measurements;
	container_t end_measurements;
};

class Timing
{
public:


	static Timing* get_instance();

	void log_start(const uint32_t uid);
	void log_end(const uint32_t uid);

	measurements_t get_measurements();

	void to_file(const std::string& filename_prefix);
	void to_xml();
	void set_name_for(const uint32_t uid,const std::string& name);
	std::string get_name(uint32_t uid);

private:

	Timing(const unsigned int e_elems);

	measurements_t timing_list;
	std::map<uint32_t,std::string> names_map;

	static Timing* instance_;
};








} /* namespace timing */

#endif /* TIMING_H_ */
