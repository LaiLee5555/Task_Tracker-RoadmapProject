First you have to install Java SDK and Adding the Path enviroment to your computer to be able to run Java
For installation method please check on java official website

## Check if Java exited in your device
  java --version
  
## Compile the file ##
  javac TaskCli.java

## Run the main file TaskCli.java ##
  java TaskCli.java (command) (agu) (agu)

There is four type of command: 
  - add : to add a new task
          cmd: java TaskCli.java add (description of the task)
          example: java TaskCli.java add "Do Homework"
    
  - list : to list all the exit task and list by filter the status
    list all:
          cmd: java TaskCli.java list
    list by Fileter the status:
          cmd: java TaskCli.java list "todo"
          cmd: java TaskCli.java list "inProgress"
          cmd: java TaskCli.java list "Done"
  - update : to update the description of the task
          cmd: java TaskCli.java upadate (id) (description)
          example: java TaskCli.java update 1 "Do Math Homwork"
  - delete : to delete the task:
          cmd: java TaskCli.java delete (id)
          example: java TaskCli.java delete 1
            The terminal will ask you to comfirm type "yes" or "no"
            "yes" to delete || "no" to cancel
          

          
