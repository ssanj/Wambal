#!/bin/bash
mvn clean install && adb install -r target/Wambal.apk
