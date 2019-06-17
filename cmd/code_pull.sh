#!/bin/sh
git pull --rebase origin develop
git submodule sync
git submodule update