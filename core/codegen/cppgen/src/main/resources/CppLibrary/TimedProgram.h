/*
 * TimedProgram.h
 *
 *  Created on: Mar 3, 2015
 *      Author: morten
 */

#ifndef INCLUDE_TIMEDPROGRAM_H_
#define INCLUDE_TIMEDPROGRAM_H_


#include "Timing.h"
#include "Stats.h"

namespace timing {


class TimedProgram
{
public:
	TimedProgram(std::initializer_list<std::pair<int,std::string>> names);

	~TimedProgram();

private:

};


} /* namespace timing */

#endif /* INCLUDE_TIMEDPROGRAM_H_ */
