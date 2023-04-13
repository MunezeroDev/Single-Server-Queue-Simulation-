//importing packages
import java.util.*;
import java.io.*;

//class declaration
public class QueueSimulation {
    //constants
	 final static int
            queue_limit = 100,
            Busy = 1,
            Idle = 0;

	private static int
            next_event_type,
            num_custs_delayed,
            num_delays_required,num_events,
            num_in_q, server_status;
    private static double
            area_num_in_q,
            area_server_status,
            mean_interarrival,
            mean_service,
            time,
            time_last_event,
            total_of_delays;
    // arrays and list
    private static double [] time_arrival = new double[ queue_limit+1 ];
    private static double [] time_next_event = new double[ 3 ];
    private static List<Double> doubles = new ArrayList<Double>();
    private static PrintWriter output;

    // Initialization method.
    private static void initialize()
    {
    	time = 0.0;

    	server_status = Idle;
        num_in_q = 0;
        time_last_event = 0.0;

        num_custs_delayed = 0;
        total_of_delays = 0.0;
        area_num_in_q = 0.0;
        area_server_status = 0.0;


        time_next_event[1] = time + expon(mean_interarrival);
        time_next_event[2] = Math.pow(100,300);
    }
    
    // Timing function 
    private static void timing()
    {
    	int i;
        double min_time_next_event = Math.pow(100,29);

        next_event_type = 0;

        // Determine the event type of the next event to occur.
        for (i = 1; i <= num_events; ++i) 
        {
        	if ((time_next_event[ i ] < min_time_next_event)) 
        	{
        		min_time_next_event = time_next_event[ i ];
        		next_event_type = i;
        	}	
        }

        // Check to see whether the event list is empty
        if (next_event_type == 0) 
        {
            output.printf("\n%s%f","Event list empty at time: ", time);
            System.exit(1);	
        }

        // The event list is not empty, so advance the simulation clock.
        time = min_time_next_event;
    }

    // Arrival event function
    private static void arrive()
    {
    	double delay;

    	time_next_event[1] = time + expon(mean_interarrival);


    	if (server_status == Busy) 
    	{
    		++num_in_q;

    		if (num_in_q > queue_limit) 
    		{
                output.printf("\n%s","Overflow of the array time_arrival at");
                output.printf("%s%f", " time: ", time );
    			System.exit(2);
    		}

    		time_arrival[num_in_q] = time;
    	}
    	else 
		{
            delay = 0.0;
            total_of_delays += delay;

            ++num_custs_delayed;
            server_status = Busy;

            time_next_event[2] = time + expon(mean_service);
		}
    }

    // Departure event function.
    private static void depart()
    {
    	int i;
        double delay;

        if (num_in_q == 0) 
        {
        	server_status = Idle;
            time_next_event[2] = Math.pow(10,30);
        }
        else
        {
        	--num_in_q;

        	delay  = time - time_arrival[1];//Compute the delay of the customer
            total_of_delays += delay;

            ++num_custs_delayed;
            time_next_event[2] = time + expon(mean_service);

            for (i = 1; i <= num_in_q; ++i) 
            {
            	time_arrival[i] = time_arrival[i + 1];
            }
        }
    }

    // Update area accumulators for time-average statistics.
    private static void update_time_avg_stats()
    {
    	double time_since_last_event;

    	time_since_last_event = time - time_last_event;
    	time_last_event = time;

    	area_num_in_q += num_in_q * time_since_last_event;

    	area_server_status += server_status * time_since_last_event;
    }

    // Exponential variate generation function
    private static double expon(double mean)
    {
    	double u;

    	u = Math.random();

    	return -mean*Math.log(u);
    }
    
        // Main Method....................
    public static void main(String[] args) throws FileNotFoundException, IOException
    {

        FileReader in = new FileReader("infile.txt");
        Scanner read = new Scanner(in);

        while (read.hasNext()) 
        {
            if (read.hasNextInt()) 
            {

                num_delays_required = read.nextInt();
            } 
            else if (read.hasNextDouble()) 
            {
                double double_val = read.nextDouble();
                doubles.add(double_val);
            }    
        }
        read.close();

        mean_interarrival = doubles.get(0);
        mean_service = doubles.get(1);
        
        // specify the number of events for the timing function.
        num_events = 2;


        PrintWriter output = new PrintWriter("outfile.txt");

        // write report heading and input parameters into output files.
        output.printf("%s\n\n", "================SINGLE-SERVER QUEUING SYSTEM============");
        output.printf("%s\n"," ======Input Variables======");
        output.printf("%s%.1f%s\n", "Mean interval time: ", mean_interarrival, " minutes");
        output.printf("%s%.1f%s\n", "Mean service time: ", mean_service, " minutes");
        output.printf("%s%d\n\n\n", "Number of customers: ", num_delays_required);

        initialize();

        while (num_custs_delayed < num_delays_required)
        {

        	timing();

        	update_time_avg_stats();

        	switch (next_event_type)
        	{
        		case 1:
                    arrive () ;
                    break;
                case 2:
                    depart();
                    break;
        	}
        }
        // reports
        output.printf("%s"," ======Output Report======");
        output.printf("\n%s%.5f%s\n", "Average delay in queue: ", total_of_delays / num_custs_delayed, " minutes");
        output.printf("%s%.5f\n", "Average number in queue: ", area_num_in_q / time);
        output.printf("%s%.5f\n", "Server utilization: ", area_server_status / time);
        output.printf("%s%.5f", "Time simulation ended: ", time);

        output.close();
    }
} 