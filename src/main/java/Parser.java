import java.time.format.DateTimeParseException;

public class Parser {
    public Parser() {

    }

    public enum Command {
        BYE, LIST, UNMARK, MARK, DELETE, TODO, DEADLINE, EVENT, UNKNOWN;
        public static Command getCategory(String input) {
            try {
                return Command.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                return UNKNOWN;
            }
        }
    }

    public static void parseCommand(String input, TaskList taskList, Ui ui) throws DukeException {
        Command category = Command.getCategory(input);
        int listSize = taskList.getSize();
        switch (category) {
            case BYE:
                break;
            case LIST:
                taskList.listTask(ui);
                break;
            case UNMARK:
                int unmarkId = ui.readInt() - 1;
                if (unmarkId < 0 || unmarkId >= listSize) {
                    try {
                        ui.setIndentedLine();
                        throw new DukeException("Sorry, please select a valid task for me to unmark!");
                    } catch (DukeException e) {
                        System.out.println("  " + e.getMessage());
                        ui.setIndentedLine();
                        return;
                    }
                }
                taskList.unmarkTask(unmarkId, ui);
                break;
            case MARK:
                int markId = ui.readInt() - 1;
                if (markId < 0 || markId >= listSize) {
                    try {
                        ui.setIndentedLine();
                        throw new DukeException("Sorry, please select a valid task for me to mark!");
                    } catch (DukeException e) {
                        System.out.println("  " + e.getMessage());
                        ui.setIndentedLine();
                        return;
                    }
                }
                taskList.markTask(markId, ui);
                break;
            case DELETE:
                int deleteId = ui.readInt() - 1;
                if (deleteId < 0 || deleteId >= listSize) {
                    try {
                        ui.setIndentedLine();
                        throw new DukeException("Sorry, please select a valid task for me to delete!");
                    } catch (DukeException e) {
                        System.out.println("  " + e.getMessage());
                        ui.setIndentedLine();
                        return;
                    }
                }
                taskList.deleteTask(deleteId, ui);
                ui.listSizeMessage(taskList);
                break;
            case TODO:
                String toDoDescription = ui.readCommandLine();
                if (toDoDescription.isEmpty()) {
                    try {
                        ui.setIndentedLine();
                        throw new DukeException("Sorry, please give me a description of the todo as well! >.<\n"
                                + "  " + "Format should be todo (description)!");
                    } catch (DukeException e) {
                        System.out.println("  " + e.getMessage());
                        ui.setIndentedLine();
                        return;
                    }
                }
                ToDo toDo = new ToDo(toDoDescription);
                taskList.addTask(toDo);
                ui.setIndentedLine();
                System.out.println("  " + toDo);
                ui.listSizeMessage(taskList);
                break;
            case DEADLINE:
                String deadlineDescription = ui.readCommandLine();
                if (!deadlineDescription.contains(" /by ")) {
                    try {
                        ui.setIndentedLine();
                        throw new DukeException("Sorry, please give me a description of the deadline as well! >.<\n"
                                + "  "
                                + "Format should be deadline (description) /by (yyyy-MM-dd HHmm!)");
                    } catch (DukeException e) {
                        System.out.println("  " + e.getMessage());
                        ui.setIndentedLine();
                        return;
                    }
                }
                String[] deadlineArguments = deadlineDescription.split(" /by ");
                String DLDescription = deadlineArguments[0];
                String dateTime = deadlineArguments[1];

                try {
                    Deadline deadline = new Deadline(DLDescription, dateTime);
                    taskList.addTask(deadline);
                    ui.setIndentedLine();
                    System.out.println("  " + deadline);
                    ui.listSizeMessage(taskList);
                    break;
                } catch (DateTimeParseException e) {
                    System.out.println("Sorry! Format should be deadline (description) /by (yyyy-MM-dd HHmm!)");
                    ui.setIndentedLine();
                    return;
                }
            case EVENT:
                String eventDescription = ui.readCommandLine();
                if (!eventDescription.contains(" /from ") || !eventDescription.contains(" /to ")) {
                    try {
                        ui.setIndentedLine();
                        throw new DukeException("Sorry, please give me a description of the event as well! >.<\n"
                                + "  "
                                + "Format should be event "
                                + "(description) /from (yyyy-MM-dd HHmm) /to (yyyy-MM-dd HHmm)!");
                    } catch (DukeException e) {
                        System.out.println("  " + e.getMessage());
                        ui.setIndentedLine();
                        return;
                    }
                }
                String[] eventArguments = eventDescription.split(" /from ");
                String[] eventDuration = eventArguments[1].split(" /to ");
                String EVDescription = eventArguments[0];
                String startTime = eventDuration[0];
                String endTime = eventDuration[1];

                try {
                    Event event = new Event(EVDescription, startTime, endTime);
                    taskList.addTask(event);
                    ui.setIndentedLine();
                    System.out.println("  " + event);
                    ui.listSizeMessage(taskList);
                    break;
                } catch (DateTimeParseException e) {
                    System.out.println("  "
                            + "Sorry! Format should be event " +
                            "(description) /from (yyyy-MM-dd HHmm) /to (yyyy-MM-dd HHmm)!");
                    ui.setIndentedLine();
                    return;
                }
            default:
                try {
                    ui.setIndentedLine();
                    throw new DukeException("Sorry, I cannot understand what this is!");
                } catch (DukeException e) {
                    System.out.println("  " + e.getMessage());
                    ui.setIndentedLine();
                }
        }
    }
}