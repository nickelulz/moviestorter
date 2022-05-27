import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(new File("list.txt"));
        ArrayList<Entry> entries = new ArrayList<Entry>();

        // Input
        in.nextLine();
        while (in.hasNextLine()) 
        {
            String line = in.nextLine();
            String name = line.substring(0, line.indexOf(":"));
            String[] movies = new String[0];

            if (line.indexOf(":") < line.length()-1) 
            {
                if (line.contains(","))
                    movies = line.substring(line.indexOf(":")+1).split(",");
                else
                    movies = new String[] { line.substring(line.indexOf(" ")) };
            } 
            else
                movies = new String[0];

            ArrayList<Movie> moviesList = new ArrayList<Movie>();
            for (String m: movies) 
            {
                m = m.trim();
                String title = "";
                int genre = -1, priority = 1, year = 0, duration = 0;

                /**
                 * N -> Film MUST be watched at night
                 * E -> Film MUST be watched "last"
                 */
                if (m.startsWith("?N") || m.startsWith("?E")) 
                {
                    // Night film
                    if (m.startsWith("?N"))
                        priority = 2;

                    // Going Last
                    else if (m.startsWith("?E"))
                        priority = 3;

                    title = m.substring(2, m.lastIndexOf(" ")).trim();
                } 
                else 
                    title = m.substring(0, m.lastIndexOf(" ")).trim();

                // Avoiding parsing errors in case it is not an integer
                if (Character.isDigit(m.charAt(m.length()-1)))
                    genre = Integer.parseInt(m.substring(m.length()-1));
                
                // Year parse - no error handling (haha... surely this won't be bad later...)
                year = Integer.parseInt(m.substring(m.indexOf("(")+1, m.indexOf(")")));
                duration = Integer.parseInt(m.substring(m.lastIndexOf("(")+1, m.lastIndexOf(")")));

                moviesList.add(new Movie(title, genre, priority, year, duration));
            }
            entries.add(new Entry(name, moviesList));
        }
        in.close();
        
        // Sort
        Collections.sort(entries);
        ArrayList<Movie> movies = new ArrayList<Movie>();
        for (Entry e: entries)
            for (Movie m: e.picks)
                movies.add(m);
        Collections.sort(movies);

        // Print
        int totalTime = 0;
        for (int i = 1; i <= movies.size(); i++) {
            System.out.println(i + ": " + movies.get(i-1).title + " (" + movies.get(i-1).year + ") - " + Movie.length(movies.get(i-1).duration));
            totalTime += movies.get(i-1).duration;
        }
        System.out.println("\nTotal estimated watchtime: " + Movie.length(totalTime));
    }
}

final class Entry implements Comparable<Entry> 
{
    public String name;
    public ArrayList<Movie> picks;

    public Entry(String name, ArrayList<Movie> picks) 
    {
        this.name = name;
        this.picks = picks;
    }

    /**
     * everyone is ordered globally based on how many suggestions 
     * they made and then we first watch everything in the first 
     * priority category (so everyone's first priority picks)
     */
    @Override
    public int compareTo(Entry other) 
    {
        if (picks.size() != other.picks.size())
            return other.picks.size() - picks.size();
        return name.compareTo(other.name);
    }
}

final class Movie implements Comparable<Movie> 
{
    public String title;
    // Genres: 0 action, 1 comedy, 2 family, 3 sigma, 4 art, 5 horror
    public int genre;
    // Priorities: 1 normal, 2 night, 3 last
    public int priority;
    public int year;
    public int duration; // minutes

    public Movie(String title, int genre, int priority, int year, int duration) 
    {
        this.title = title;
        this.genre = genre;
        this.priority = priority;
        this.year = year;
        this.duration = duration;
    }

    @Override
    public int compareTo(Movie other) 
    {
        if (priority != other.priority)
            return priority - other.priority;
        else if (genre != other.genre)
            return genre - other.genre;
        else
            return 0;
    }

    public static String length(int duration) {
        int hours = duration / 60;
        int seconds = duration % 60;
        String out = "";
        if (hours != 0)
            out += hours + "h ";
        if (out == "" || seconds != 0)
            out += seconds + "s";
        return out;
    }
}