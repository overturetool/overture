/*
 * IO.hpp
 *
 *  Created on: Jan 19, 2015
 *      Author: morten
 */

#ifndef IO_HPP_
#define IO_HPP_

#include "vdm_types.hpp"
#include <string>

class IO
{
public:

	template<typename T>
	static void print(const T& str)
	{
		std::cout << str;
	}

	template<typename T>
	static void println(const T& str)
	{
		std::cout <<str << std::endl;
	};
};


#endif /* IO_HPP_ */
