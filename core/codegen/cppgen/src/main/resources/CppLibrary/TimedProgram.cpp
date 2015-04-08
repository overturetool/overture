/*
 * TimedProgram.cpp
 *
 *  Created on: Mar 3, 2015
 *      Author: morten
 */

#include <TimedProgram.h>

namespace timing {

TimedProgram::TimedProgram(std::initializer_list<std::pair<int,std::string>> names)
{
	for(auto it = names.begin();it!= names.end();++it)
	{
		timing::Timing::get_instance()->set_name_for(it->first,it->second);
	}
}

TimedProgram::~TimedProgram()
{
	timing::measurements_t meas = timing::Timing::get_instance()->get_measurements();
	timing::stats::print_stats(meas);
	timing::Timing::get_instance()->to_file("mem-");
	timing::stats::stats_to_xml(meas);
}


} /* namespace timing */
