import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ProducerConsumer
 * <p/>
 * Producer and consumer tasks in a desktop search application
 *
 * @author Brian Goetz and Tim Peierls
 */
public class ProducerConsumer {
    /**
     * Producer class that explores directory and feeds the consumer (indexer)
     */
    static class FileCrawler implements Runnable {
        /**
         * BlockingQueue for the list of files
         */
        private final BlockingQueue<File> fileQueue;
        /**
         * filter for filecrawler
         */
        private final FileFilter fileFilter;
        /**
         * root file for filecrawler
         */
        private final File root;
        /**
         * poison pill file for consumer class exit
         */
        private final File poisonFile=new File("");

        /**
         * constructor for filecrawler
         * @param fileQueue
         * @param fileFilter
         * @param root
         */
        public FileCrawler(BlockingQueue<File> fileQueue,
                           final FileFilter fileFilter,
                           File root) {
            this.fileQueue = fileQueue;
            this.root = root;
            this.fileFilter = new FileFilter() {
                public boolean accept(File f) {
                    return f.isDirectory() || fileFilter.accept(f);
                }
            };
        }

        /**
         * check if file is indexed
         * @param f
         * @return
         */
        private boolean alreadyIndexed(File f) {
            return false;
        }

        /**
         * main process of disk crawler, adds poison pill after crawling through all files
         */
        public void run() {
            try {
                crawl(root);

                for (int i = 0; i < N_CONSUMERS; i++) {
                    fileQueue.put(poisonFile);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Files found : "+fileCounter);
        }

        /**
         * Recursively crawls through the root directory and adds files (not directories) to blockingqueue
         * @param root
         * @throws InterruptedException
         */
        private void crawl(File root) throws InterruptedException {
            File[] entries = root.listFiles(fileFilter);
            if (entries != null) {
                for (File entry : entries) {
                    if (entry.isDirectory()) {
                        crawl(entry);
                    } else if (!alreadyIndexed(entry)) {
                        fileQueue.put(entry);
                    }
                }
            }

        }

    }

    /**
     * Consumer class that indexes file
     */
    static class Indexer implements Runnable {
        /**
         * BlockingQueue for files
         */
        private final BlockingQueue<File> queue;

        /**
         * Constructor for indexer class
         * @param queue
         */
        public Indexer(BlockingQueue<File> queue) {
            this.queue = queue;
        }

        /**
         * File being taken from BlockingQueue
         */
        private File filePeek;

        /**
         * Main process of indexer,exits when poison pill is recieved
         */
        public void run() {
            try {
                while (true){
                    filePeek=queue.take();
                    if(!filePeek.exists()){
                        break;
                    }
                    indexFile(filePeek);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        /**
         * Indexes file, increase file count
         * @param file
         */
        public void indexFile(File file) {
            // Index the file...
            fileCounter.incrementAndGet();
        };
    }

    /**
     *  AtomicInteger to count files
     */
    private static AtomicInteger fileCounter=new AtomicInteger(0);
    /**
     * Bound for blockingqueue
     */
    private static final int BOUND = 10;
    /**
     * Number of consumers based on processors
     */
    private static final int N_CONSUMERS = Runtime.getRuntime().availableProcessors();

    /**
     * Start indexing from array of root directories, starts the diskcrawler and indexer threads
     * @param roots
     */
    public static void startIndexing(File[] roots) {
        BlockingQueue<File> queue = new LinkedBlockingQueue<File>(BOUND);
        FileFilter filter = new FileFilter() {
            public boolean accept(File file) {
                return true;
            }
        };

        for (File root : roots) {
            new Thread(new FileCrawler(queue, filter, root)).start();
        }
        for (int i = 0; i < N_CONSUMERS; i++) {
            new Thread(new Indexer(queue)).start();
        }
    }
}
