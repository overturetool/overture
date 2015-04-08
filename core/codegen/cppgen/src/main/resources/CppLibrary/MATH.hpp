/*
 * MATH.hpp
 *
 *  Created on: Feb 10, 2015
 *      Author: morten
 */

#ifndef INCLUDE_MATH_HPP_
#define INCLUDE_MATH_HPP_

#include <cstdlib>
namespace MATH
{


const std::random_device rd;     // only used once to initialise engine


const double pi = M_PI;

double atan(const double v)
{
	return ::atan(v);
}

double cos(const double v)
{
	return ::cos(v);
}

double sin(const double v)
{
	return ::sin(v);
}

double sqrt(const double v)
{
	return ::sqrt(v);
}

int rand(int s)
{
	if(s == 0)
	{
		return 0;
	}

    return ::rand() %s +1;
}




}



#endif /* INCLUDE_MATH_HPP_ */
