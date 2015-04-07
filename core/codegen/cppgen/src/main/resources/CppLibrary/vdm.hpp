/*
 * vdm.hpp
 *
 *  Created on: Feb 10, 2015
 *      Author: morten
 */

#ifndef INCLUDE_VDM_HPP_
#define INCLUDE_VDM_HPP_

#include <vector>
#include <list>
#include <map>
#include <algorithm>
#include <vdm_types.hpp>
#include <sstream>
#include <initializer_list>

namespace vdm
{

template<typename T, typename T2,typename T3>
bool compatible(const T3& v)
{
	return true;
}



template<typename T>
std::set<typename T::value_type> elems(std::initializer_list<typename T::value_type> elems)
{
	return std::set<typename T::value_type>(elems);

}

void Runtime2(std::string msg,std::string file,int line)
{
	std::string s;
	std::stringstream ss;
	ss << msg << " File: " << file << " Line: "<< line << std::endl;
}


template<typename T>
int len(const T& v)
{
	return v.size();
}

template<typename T>
typename T::value_type head(const T& v)
{
	return v[0];
}

template<typename T>
T tail(const T& v)
{
	T new_;
	if(v.size() == 0)
	{
		return T();
	}
	else if(v.size() == 1)
	{
		std::copy(v.begin(),v.end(),std::back_inserter(new_));
		return new_;
	}
	else
	{
		std::copy(++v.begin(),v.end(),std::back_inserter(new_));
		return new_;
	}

}

template<typename T>
T concat(const T& l, const T& r)
{
	T new_;
	auto insert = std::back_inserter(new_);
	std::copy(l.begin(),l.end(),insert);
	std::copy(r.begin(),r.end(),insert);
	return new_;
}

}


#endif /* INCLUDE_VDM_HPP_ */
