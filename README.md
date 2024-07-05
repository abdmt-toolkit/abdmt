# Ability-Based Design Mobile Toolkit (ABD-MT)

## Installation

Download the toolkit source code from [https://github.com/abdmt-toolkit/abdmt/](https://github.com/abdmt-toolkit/abdmt/)

## Getting Started

Follow the instructions at [https://github.com/abdmt-toolkit/abdmt/wiki/1.-Getting-Started](https://github.com/abdmt-toolkit/abdmt/wiki/1.-Getting-Started)

## API Methods

ABD-MT has three main modules: the _Observers_, the _Ability Modeler_, and the _UI Adapter_. Observers record input events and sensor data, currently for four types of abilities: touch, gesture, physical activity, and attention. Each Observer also provides ability information through calculation of human performance metrics. The Ability Modeler synthesizes captured interaction data and metrics from Observers to model a user's abilities, exporting methods for developers to inspect those abilities. For example, an app can query the Ability Modeler as to whether a user exhibits tremor, and whether this might be due to walking or running. Finally, the UI Adapter allows developers to manipulate UI widgets and layouts according to observed abilities and behaviors. 

The full documentation can be found at the [project wiki](https://github.com/abdmt-toolkit/abdmt/wiki).

### Observers

For a full list of Observer methods, check [Observer Methods](https://github.com/abdmt-toolkit/abdmt/wiki/2.-Observer-Methods).

### Ability Modeler

For a full list of Ability Modeler methods, check [Ability Modeler Methods](https://github.com/abdmt-toolkit/abdmt/wiki/3.-Ability-Modeler-Methods).

### UI Adapter

For a full list of UI Adapter methods, check [UI Adapter Methods](https://github.com/abdmt-toolkit/abdmt/wiki/4.-UI-Adapter-Methods).
