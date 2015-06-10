/*
 * TimedScope.cpp
 *
 *  Created on: Feb 10, 2015
 *      Author: morten
 */

#include <TimedScope.hpp>

TimedScope::TimedScope(uint32_t uid): uid_(uid)
{
	timing::Timing::get_instance()->log_start(uid_);
}

TimedScope::~TimedScope()
{
	timing::Timing::get_instance()->log_end(uid_);
}
