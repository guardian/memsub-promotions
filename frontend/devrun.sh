#!/usr/bin/env bash

yarn devrun &
cd ..; sbt -mem 2048 devrun
