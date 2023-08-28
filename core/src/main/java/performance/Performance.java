package performance;


import utils.FileUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;

public class Performance {

    private class Stage {
        String name;
        String body;
        public Stage(String name, String body){
            this.body=body;
            this.name = name;
        }
        public String toJson(){
            return String.format("{\"name\":\"%s\" , \"body\":\"%s\"}", name,body);
        }
        public String toKeyVal(){
            return String.format("\"%s\":\"%s\"",name,body);
        }
    }
    private Instant startTime= null;
    private Instant endTime= null;
    private ArrayList <Stage> stages=null;
    private ArrayList <Stage> metadata=null;
    private String lastMemoryUsage=null;

    public String getMemoryUsage(){

        Runtime rt = Runtime.getRuntime();

        int mb = 1024 * 1024;
        long total = rt.totalMemory();
        long free = rt.freeMemory();
        long max = rt.maxMemory();
        String re=String.format("Total: %s MB, Free: %s MB, Used: %s MB Max: %s MB",
                total/mb,
                free/mb,
                (total-free)/mb,
                max/mb);
        return re;
    }
    public void printMemoryUsage(){
        System.out.println(getMemoryUsage());
    }

    public void printLastMemoryUsage(){
        System.out.println(lastMemoryUsage);
    }
    public  void  saveLastMemoryUsage(){
        lastMemoryUsage = getMemoryUsage();
    }
    public void start(){
        startTime = Instant.now();
    }
    public void stop(){
        endTime = Instant.now();
    }
    public void restart(){
        startTime=null; endTime = null;
    }

    public void addMetadata(String name, String description){

        if(metadata==null){
            metadata = new ArrayList<Stage>();
        }

        metadata.add(new Stage(name,description));
    }

    public String getDuration(){
        return Duration.between(startTime, endTime).toString();
    }
    public void printDuration(){
        System.out.println("Elapsed Time: "+ getDuration());
    }

    public void addStageEndTime(String name){

        System.out.println("End task = " + name);
        stop();
        String duration= getDuration();
        Stage s = new Stage(name, duration);
        if(stages==null){
            stages = new ArrayList<Stage>();
        }
        stages.add(s);
        restart();
        start();
    }

    public String getStagesJSON(){
        if(stages!= null){
            String items= "";
            Iterator<Stage> iter = stages.iterator();
            while (iter.hasNext()){
                Stage s = iter.next();
                items += s.toJson();
                if(iter.hasNext()){// not the last item
                    items += ",";
                }
            }
            return items;
        }
        return null;
    }
    public String getMetadataJSON(){
        if(metadata!= null){
            String items= "";
            Iterator<Stage> iter = metadata.iterator();
            while (iter.hasNext()){
                Stage s = iter.next();
                items += s.toKeyVal();
                if(iter.hasNext()){// not the last item
                    items += ",";
                }
            }
            return items;
        }

        return  null;
    }


    public String getReportJSON(){
        addMetadata("totalTime",calculateTotalTime());
        return  String.format("{%s, \"stages\":[%s]}", getMetadataJSON(),getStagesJSON());
    }

    public void printStages(){
        System.out.println(getReportJSON());
    }

    public void writeStages(String path){
        FileUtil.writeFile(path, getReportJSON());
    }

    public String calculateTotalTime(){
        if(stages!= null){

            Iterator<Stage> iter = stages.iterator();
            Duration accum = Duration.ofSeconds(0);
            while (iter.hasNext()){
                Stage s = iter.next();
                Duration t = Duration.parse(s.body);
                accum= accum.plus(t);
            }
            return  accum.toString();
        }
        return  null;
    }

    public static void main(String args){
        /*String a=  "PT1.792751312S";
        String b=  "PT9.938449963S";

        Duration accum = Duration.ofSeconds(0);

        Duration t1 = Duration.parse(a);
        */
    }
}

