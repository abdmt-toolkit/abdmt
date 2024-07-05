# Ability-Based Design Mobile Toolkit (ABD-MT)

## Installation

Download the toolkit source code from [https://github.com/abdmt-toolkit/abdmt/](https://github.com/abdmt-toolkit/abdmt/)

## Getting Started

Follow the instructions at [https://github.com/abdmt-toolkit/abdmt/wiki/1.-Getting-Started](https://github.com/abdmt-toolkit/abdmt/wiki/1.-Getting-Started)

## API Methods

ABD-MT has three main modules: the _Observers_, the _Ability Modeler_, and the _UI Adapter_. Observers record input events and sensor data, currently for four types of abilities: touch, gesture, physical activity, and attention. Each Observer also provides ability information through calculation of human performance metrics. The Ability Modeler synthesizes captured interaction data and metrics from Observers to model a user's abilities, exporting methods for developers to inspect those abilities. For example, an app can query the Ability Modeler as to whether a user exhibits tremor, and whether this might be due to walking or running. Finally, the UI Adapter allows developers to manipulate UI widgets and layouts according to observed abilities and behaviors. 

### Observers

For the Observer methods, check [https://github.com/abdmt-toolkit/abdmt/wiki/2.-Observer-Methods](https://github.com/abdmt-toolkit/abdmt/wiki/2.-Observer-Methods).

### Ability Modeler

For the Ability Modeler methods, check [https://github.com/abdmt-toolkit/abdmt/wiki/3.-Ability-Modeler-Methods](https://github.com/abdmt-toolkit/abdmt/wiki/3.-Ability-Modeler-Methods).

### UI Adapter

For the UI Adapter methods, check [https://github.com/abdmt-toolkit/abdmt/wiki/4.-UI-Adapter-Methods](https://github.com/abdmt-toolkit/abdmt/wiki/4.-UI-Adapter-Methods).
 
For full documentation, check [https://github.com/abdmt-toolkit/abdmt/wiki](https://github.com/abdmt-toolkit/abdmt/wiki).
