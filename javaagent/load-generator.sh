#!/bin/bash

while :
do
  echo "GET http://localhost:8080/ping"
  curl "http://localhost:8080/ping" || true
  echo

  echo "GET http://localhost:8080/actuator/health"
  curl "http://localhost:8080/actuator/health" || true
  echo

  sleep 2
done