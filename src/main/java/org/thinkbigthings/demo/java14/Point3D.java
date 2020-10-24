package org.thinkbigthings.demo.java14;
// Can define records in their own files OR where needed
// Define in own file if need to share it around (maybe as DTO across module boundaries)
// Define inline for e.g. stream joining.

// if you want this available outside the package, it needs to be declared public just like a regular class
record Point3D(float x, float y, float z) {}

