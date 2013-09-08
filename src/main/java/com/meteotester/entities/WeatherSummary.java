package com.meteotester.entities;

import java.util.Date;

public class WeatherSummary {
double[] mintemp;
double[] maxtemp;
double[] qpf;
double[] pop;
String[] icon;
Date today;
int offset;
String sourceDomain;
Date timestamp;

public WeatherSummary(double[] mintemp, double[] maxtemp, double[] qpf) {
	super();
	this.mintemp = mintemp;
	this.maxtemp = maxtemp;
	this.qpf = qpf;
}

public double[] getMintemp() {
	return mintemp;
}

public void setMintemp(double[] mintemp) {
	this.mintemp = mintemp;
}

public double[] getMaxtemp() {
	return maxtemp;
}

public void setMaxtemp(double[] maxtemp) {
	this.maxtemp = maxtemp;
}

public double[] getQpf() {
	return qpf;
}

public void setQpf(double[] qpf) {
	this.qpf = qpf;
}

public double[] getPop() {
	return pop;
}

public void setPop(double[] pop) {
	this.pop = pop;
}

public String[] getIcon() {
	return icon;
}

public void setIcon(String[] icon) {
	this.icon = icon;
}

public Date getToday() {
	return today;
}

public void setToday(Date today) {
	this.today = today;
}

public int getOffset() {
	return offset;
}

public void setOffset(int offset) {
	this.offset = offset;
}

public String getSourceDomain() {
	return sourceDomain;
}

public void setSourceName(String sourceDomain) {
	this.sourceDomain = sourceDomain;
}

public Date getTimestamp() {
	return timestamp;
}

public void setTimestamp(Date timestamp) {
	this.timestamp = timestamp;
}
}
