/*
 * CSV.hpp
 *
 *  Created on: Feb 10, 2015
 *      Author: morten
 */

#ifndef INCLUDE_CSV_HPP_
#define INCLUDE_CSV_HPP_

#include <fstream>
#include <boost/tokenizer.hpp>
#include <boost/lexical_cast.hpp>
namespace CSV
{

std::tuple<bool,int> flinecount(const std::string& fname)
{
	int i  =0;
	std::ifstream infile(fname);
	for(std::string line; std::getline(infile,line);)
	{
		i++;
	}

	return std::make_tuple(true,i);
};


template<typename T>
T freadval(const std::string& fname,int i)
{

};


std::tuple<bool, std::vector<double>> freadval(const std::string& fname,int i)
{
	int in_  = 1;
	std::ifstream infile(fname);
	std::vector<double> vals;

	 typedef boost::tokenizer< boost::char_separator<char> > Tokenizer;
	 boost::char_separator<char> sep(",");

	for(std::string line; std::getline(infile,line);)
	{
		if(in_ == i )
		{
			Tokenizer tok(line,sep);
			for(Tokenizer::iterator it = tok.begin(); it!=tok.end(); ++it)
			{
				try
				{
				vals.push_back(boost::lexical_cast<double>(*it));
				}catch(boost::bad_lexical_cast& e)
				{
					std::cout << line<< std::endl;
					std::cout << e.what() <<std::endl;
					exit(1);
				}
			}

		}
		in_++;
	}

	return std::make_tuple(true,vals);
};




};



#endif /* INCLUDE_CSV_HPP_ */
