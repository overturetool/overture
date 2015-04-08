/*
 * VDMUtin.hpp
 *
 *  Created on: Mar 5, 2015
 *      Author: morten
 */

#ifndef INCLUDE_VDMUTIL_HPP_
#define INCLUDE_VDMUTIL_HPP_


namespace VDMUtil
{
std::string val2seq_of_char(int val)
{
	std::stringstream ss;

	ss << val;

	return ss.str();
};

}


#endif /* INCLUDE_VDMUTIL_HPP_ */
