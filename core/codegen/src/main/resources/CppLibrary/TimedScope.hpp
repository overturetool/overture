/*
 * TimedScope.h
 *
 *  Created on: Feb 10, 2015
 *      Author: morten
 */

#ifndef INCLUDE_TIMEDSCOPE_HPP_
#define INCLUDE_TIMEDSCOPE_HPP_
#include <Timing.h>
class TimedScope {

	public:

		TimedScope(const uint32_t uid);

		~TimedScope();

	private:
		uint32_t uid_;
};

#endif /* INCLUDE_TIMEDSCOPE_HPP_ */
