# 公共的walks

您可以通过 FileVisitor 接口轻松实现一组常用步骤。 

这个本节介绍如何编写和实现应用程序以执行文件搜索，递归副本，递归移动和递归删除。

##  搜索

大多数操作系统都提供了用于搜索文件的专用工具（例如，Linux具有命令，而Windows具有文件搜索工具）。 

从简单搜索到高级搜索，所有这些工具通常以相同的方式工作：您指定搜索条件，然后等待该工具找到匹配的文件。 

但是，如果您需要以编程方式完成搜索，则FileVisitor可以帮助您完成遍历过程。 

无论您是按名称，扩展名还是文件名查找文件全局模式或在文件内部查找某些文本或代码，方法是始终访问文件存储并执行一些检查以确定文件是否符合搜索条件。

当您基于FileVisitor编写文件搜索工具时，需要牢记以下几点：

-   visitFile()

visitFile()方法是执行以下操作之间比较的最佳位置当前文件和您的搜索条件。 

此时，您可以提取每个文件名，其扩展名或属性，或打开文件进行读取。 

您可以使用文件名称，扩展名等，用于确定是否搜索了访问的文件

有时，您会将这些信息混入复杂的搜索条件中。 

这个方法找不到目录。

-   preVisitDirectory() or postVisitDirectory()

如果要查找目录，则必须在目录中进行比较。

preVisitDirectory（）或postVisitDirectory（）方法，视情况而定。

-   visitFileFailed()

如果无法访问文件，则visitFileFailed（）方法应返回FileVisitResult.CONTINUE，因为此问题不需要整个搜索
要停止的过程。

-   FileVisitResult.CONTINUE

如果您按名称搜索文件，并且知道有一个文件文件树中的名称，则可以在文件树中返回FileVisitResult.TERMINATE
visitFile（）方法找到它。 

否则，FileVisitResult.CONTINUE 应该是回到。

-   符号链接

搜索过程可以遵循符号链接，这可能是一个好主意，因为以下符号链接可能会在遍历符号链接的目标子树。 

遵循符号链接并不总是一个好主意。

例如，不建议删除文件。

### 文件名称搜索

可以将前面的列表合并到下面的单个代码段中以生成应用程序按名称搜索文件。 

此应用程序将在整个默认设置中搜索文件rafa_1.jpg文件系统，找到后将停止搜索。

```Java
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

class Search implements FileVisitor {
    private final Path searchedFile;
    public boolean found;

    public Search(Path searchedFile) {
        this.searchedFile = searchedFile;
        this.found = false;
    }

    void search(Path file) throws IOException {
        Path name = file.getFileName();
        if (name != null && name.equals(searchedFile)) {
            System.out.println("Searched file was found: " + searchedFile + " in " + file.toRealPath().toString());
            found = true;
        }
    }

    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException exc)
        throws IOException {
            System.out.println("Visited: " + (Path) dir);
        return FileVisitResult.CONTINUE;
    }
        
    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs)
        throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs)
        throws IOException {
        
        search((Path) file);
        
        if (!found) {
            return FileVisitResult.CONTINUE;
        } else {
            return FileVisitResult.TERMINATE;
        }
    }

    @Override
        public FileVisitResult visitFileFailed(Object file, IOException exc)
        throws IOException {
        //report an error if necessary
            return FileVisitResult.CONTINUE;
        }
    }

    class Main {
        public static void main(String[] args) throws IOException {
            
            Path searchFile = Paths.get("rafa_1.jpg");
            Search walk = new Search(searchFile);
            EnumSet opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
            Iterable<Path> dirs = FileSystems.getDefault().getRootDirectories();
        
            for (Path root : dirs) {
                if (!walk.found) {
                    Files.walkFileTree(root, opts, Integer.MAX_VALUE, walk);
                }
            }

            if (!walk.found) {
                System.out.println("The file " + searchFile + " was not found!");
            }
        }
    }

```

输出的一部分可能看起来像这样：

```base
Visited: C:\Python25\Tools\webchecker

Visited: C:\Python25\Tools

Visited: C:\Python25
…

Visited: C:\rafaelnadal\equipment

Visited: C:\rafaelnadal\grandslam\AustralianOpen

Visited: C:\rafaelnadal\grandslam\RolandGarros

Visited: C:\rafaelnadal\grandslam\USOpen

Visited: C:\rafaelnadal\grandslam\Wimbledon

Visited: C:\rafaelnadal\grandslam

-------------------------------------------------------------

Searched file was found: rafa_1.jpg in C:\rafaelnadal\photos\rafa_1.jpg
```

### Glob 表达式搜索

有时，您可能只拥有有关要搜索的文件的部分信息，例如仅名称或扩展名，或者甚至只是其名称或扩展名的卡盘。 

基于这小块信息，您可以编写全局模式，如第4章“列出内容”中所述搜索将在文件存储中找到与glob模式匹配的所有文件，并且从结果中，您可能可以找到所需的文件。

以下代码段搜索C：\ rafaelnadal文件树中所有 *.jpg 类型的文件。 

仅在遍历整个树之后，进程才会停止。

```Java
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

class Search implements FileVisitor {
    private final PathMatcher matcher;
    
    public Search(String glob) {
        matcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);
    }

    void search(Path file) throws IOException {
        Path name = file.getFileName();
        if (name != null && matcher.matches(name)) {
            System.out.println("Searched file was found: " + name + " in " + file.toRealPath().toString());
        }
    }

    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException exc) throws IOException {
        System.out.println("Visited: " + (Path) dir);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
        search((Path) file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Object file, IOException exc) throws IOException {
        //report an error if necessary
        return FileVisitResult.CONTINUE;
    }
}


class Main {

    public static void main(String[] args) throws IOException {
        String glob = "*.jpg";
        Path fileTree = Paths.get("C:/rafaelnadal/");
        Search walk = new Search(glob);
        EnumSet opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        Files.walkFileTree(fileTree, opts, Integer.MAX_VALUE, walk);
    }
}

```

输出的片段显示找到的文件：

```base
Searched file was found: rafa_1.jpg in C:\rafaelnadal\photos\rafa_1.jpg

Searched file was found: rafa_winner.jpg in C:\rafaelnadal\photos\rafa_winner.jpg
```


如果您有有关所需文件的其他信息，则可以创建更多复杂的搜索。 

例如，除了有关文件名和类型的一小部分信息外，也许您知道文件大小小于一定的千字节数，或者您知道详细信息，例如创建文件的时间，上次修改文件的时间，隐藏文件还是隐藏文件只读或谁拥有它。 

附加信息可能是文件属性的一部分，如以下代码段将* .jpg全局模式与文件大小小于100KB（如您可能知道，大小是基本属性）：

```Java
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

class Search implements FileVisitor {
    private final PathMatcher matcher;
    private final long accepted_size;

    public Search(String glob, long accepted_size) {
        matcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);
        this.accepted_size = accepted_size;
    }

    void search(Path file) throws IOException {
        Path name = file.getFileName();
        long size = (Long) Files.getAttribute(file, "basic:size");

        if (name != null && matcher.matches(name) && size <= accepted_size) {
            System.out.println("Searched file was found: " + name + " in " +
            file.toRealPath().toString() + " size (bytes):" + size);
        }
    }


    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException exc) throws IOException {
        System.out.println("Visited: " + (Path) dir);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
        search((Path) file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Object file, IOException exc) throws IOException {
        //report an error if necessary
        return FileVisitResult.CONTINUE;
    }
}

class Main {
    public static void main(String[] args) throws IOException {
        String glob = "*.jpg";
        long size = 102400; //100 kilobytes in bytes
        Path fileTree = Paths.get("C:/rafaelnadal/");
        Search walk = new Search(glob, size);
        EnumSet opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        Files.walkFileTree(fileTree, opts, Integer.MAX_VALUE, walk);
    }
}
```

以下是找到的文件输出的一部分：

```base
Searched file was found: rafa_winner.jpg in C:\rafaelnadal\photos\rafa_winner.jpg size (bytes):77718
```

### 按内容搜索文件

高级文件搜索之一涉及按文件内容查找文件。

您传递了一系列单词或句子，搜索仅返回包含该文本的文件。

这是最耗时的文件搜索任务，因为它需要在每个访问的文件中搜索文本，这意味着打开文件，阅读它，最后关闭它。

此外，有许多支持文本的文件格式，例如PDF，Microsoft Word，Excel和PowerPoint，简单文本文件，XML，HTML，XHTML等。

每个这些格式的读取方式有所不同，这需要能够提取文本文件的专用代码从他们。

在本节中，我们将开发一个按内容搜索文件的应用程序。

要搜索的文字以字符串形式传递，该字符串包含由逗号分隔的单词或句子序列；例如：“Rafael Nadal,tennis,winner of Roland Garros,BNP Paribas tournament draws。”

使用StringTokenizer类，并用逗号作为分隔符，下面的示例提取每个单词并一句话成ArrayList：

```Java
String words="Rafael Nadal,tennis,winner of Roland Garros,BNP Paribas tournament draws";
ArrayList<String> wordsarray = new ArrayList<>();
…
StringTokenizer st = new StringTokenizer(words, ",");

while (st.hasMoreTokens()) {
    wordsarray.add(st.nextToken());
}
```

以下代码循环此ArrayList并将每个单词和句子与文本进行比较从访问的文件中提取。 

请注意，在searchText（）方法中，提取的文本作为参数。

```Java
//search text
private boolean searchText(String text) {
    boolean flag = false;

    for (int j = 0; j < wordsarray.size(); j++) {
        if ((text.toLowerCase()).contains(wordsarray.get(j).toLowerCase())) {
            flag = true;
            break;
        }
    }
    return flag;
}
```

以下各小节着重介绍了从一些方法中提取文本的方法。

最常见的文件格式并进行比较。 

由于我们不打算在这里重新发明轮子，因此我们将利用一些专门为理解以下内容编写的第三方库：特定的文件格式。 

然后，我们将把开发的每种方法组合成一个完整的搜索程序。

####    PDF

为了阅读PDF文件，我们将使用两个最受欢迎的第三方开源库iText和Apache PDFBox。 

您可以从http://itextpdf.com/下载iText库和PDFBox库，来自http://pdfbox.apache.org/。 

为了本章的目的，我使用了iText的5.1.2版本和PDFBox的1.6.0。 

根据iText文档，我编写了以下方法从PDF。 

第一步包括在访问的文件上创建一个PdfReader。 

继续提取PDF文件页数，从每个页面提取文本，然后将提取的文本传递给searchText（）方法。 

如果在提取的文本中找到标记之一，则当前文件的搜索将停止，该文件被认为是有效的搜索结果，其路径和名称已存储，因此我们可以稍后将其打印出来整个搜索结束后。

```Java
//search in PDF files using iText library
boolean searchInPDF_iText(String file) {
    PdfReader reader = null;
    boolean flag = false;

    try {
        reader = new PdfReader(file);
        int n = reader.getNumberOfPages();
        OUTERMOST:
    
    for (int i = 1; i <= n; i++) {
        String str = PdfTextExtractor.getTextFromPage(reader, i);
        flag = searchText(str);
        if (flag) {
            break OUTERMOST;
        }      
    }

    } catch (Exception e) {
    } finally {
        if (reader != null) {
        reader.close();
    }
    return flag;
    }
}
```

如果您比iText更熟悉PDFBox，请尝试以下方法。 

首先创建一个在PDF文件上使用PDFParser，然后继续提取页数，然后继续提取文本并将其传递给searchText（）方法。

```Java
boolean searchInPDF_PDFBox(String file) {
    PDFParser parser = null;
    String parsedText = null;
    PDFTextStripper pdfStripper = null;
    PDDocument pdDoc = null;
    COSDocument cosDoc = null;
    boolean flag = false;
    int page = 0;

    File pdf = new File(file);

    try {
        parser = new PDFParser(new FileInputStream(pdf));
        parser.parse();
        cosDoc = parser.getDocument();
        pdfStripper = new PDFTextStripper();
        pdDoc = new PDDocument(cosDoc);
        OUTERMOST:

    while (page < pdDoc.getNumberOfPages()) {
        page++;
        pdfStripper.setStartPage(page);
        pdfStripper.setEndPage(page + 1);
        parsedText = pdfStripper.getText(pdDoc);
        flag = searchText(parsedText);

        if (flag) {
            break OUTERMOST;
        }
        }

    } catch (Exception e) {
    } finally {
        try {
            if (cosDoc != null) {
                cosDoc.close();
            }
            if (pdDoc != null) {
                pdDoc.close();
            }
    } catch (Exception e) {}
        return flag;
    }
}
```

####    Microsoft Word, Excel, and PowerPoint Files

可以通过Apache POI库来操作Microsoft Office套件的文件，这是目前最多的Microsoft文档的常用Java API。 

您可以从以下位置下载该库http://poi.apache.org/。 

在本章中，我使用3.7版。 

根据开发者指南，我编写了以下用于从Word文档提取文本的方法。 

Apache POI提取一个数组包含Word文档所有段落的字符串。 

数组可以循环，每个段落可以传递给searchText（）方法。

```Java
boolean searchInWord(String file) {
    POIFSFileSystem fs = null;
    boolean flag = false;

    try {
        fs = new POIFSFileSystem(new FileInputStream(file));
        HWPFDocument doc = new HWPFDocument(fs);
        WordExtractor we = new WordExtractor(doc);
        String[] paragraphs = we.getParagraphText();
        OUTERMOST:

    for (int i = 0; i < paragraphs.length; i++) {
        flag = searchText(paragraphs[i]);
        if (flag) {
            break OUTERMOST;
        }
    }

    } catch (Exception e) {
    } finally {
        return flag;
    }
}
```

我们可以从Excel文件中提取文本，如下例所示。 

创建一个用于Excel文档的HSSFWorkbook，其基本思想是依次遍历工作表，行和终于在牢房上空了。 

该单元格应包含我们要查找的特定文本。

```Java
boolean searchInExcel(String file) {
    Row row;
    Cell cell;
    String text;
    boolean flag = false;
    InputStream xls = null;

    try {
        xls = new FileInputStream(file);
        HSSFWorkbook wb = new HSSFWorkbook(xls);
        int sheets = wb.getNumberOfSheets();
        OUTERMOST:

    for (int i = 0; i < sheets; i++) {
        HSSFSheet sheet = wb.getSheetAt(i);
        Iterator<Row> row_iterator = sheet.rowIterator();

        while (row_iterator.hasNext()) {
            row = (Row) row_iterator.next();
            Iterator<Cell> cell_iterator = row.cellIterator();

            while (cell_iterator.hasNext()) {
                cell = cell_iterator.next();
                int type = cell.getCellType();
                if (type == HSSFCell.CELL_TYPE_STRING) {
                        text = cell.getStringCellValue();
                        flag = searchText(text);
                    if (flag) {
                        break OUTERMOST;
                    }
                }
            }
        }
    }
    } catch (IOException e) {
    } finally {
        try {
            if (xls != null) {
                xls.close();
            }
    } catch (IOException e) {}
        return flag;
    }
}

```

最后，我们可以从PowerPoint文件中提取文本，如以下示例所示； 每个幻灯片可能包含文字和注释：

```Java
boolean searchInPPT(String file) {
    boolean flag = false;
    InputStream fis = null;
    String text;

    try {
        fis = new FileInputStream(new File(file));
        POIFSFileSystem fs = new POIFSFileSystem(fis);
        HSLFSlideShow show = new HSLFSlideShow(fs);
        SlideShow ss = new SlideShow(show);
        Slide[] slides = ss.getSlides();
        OUTERMOST:

        for (int i = 0; i < slides.length; i++) {
                TextRun[] runs = slides[i].getTextRuns();

            for (int j = 0; j < runs.length; j++) {
                TextRun run = runs[j];
                if (run.getRunType() == TextHeaderAtom.TITLE_TYPE) {
                    text = run.getText();
                } else {
                    text = run.getRunType() + " " + run.getText();
                }
                flag = searchText(text);
                if (flag) {
                    break OUTERMOST;
                }
            }

            Notes notes = slides[i].getNotesSheet();

            if (notes != null) {
                runs = notes.getTextRuns();

                for (int j = 0; j < runs.length; j++) {
                    text = runs[j].getText();
                    flag = searchText(text);

                    if (flag) {
                        break OUTERMOST;
                    }
                }
            }
        }
    } catch (IOException e) {
    } finally {
        try {
            if (fis != null) {
                fis.close();
        }
    } catch (IOException e) {}
        return flag;
    }
}
```

我任意选择了前面示例中使用的第三方库。 

还有很多其他开放可用于处理各种文档的源库和商业库。 

随意使用任何东西方便您的需求。 我

们的搜索示例不是执行搜索的最有效方法。 

在最坏的情况下场景中，我们将必须遍历整个数组（在典型场景中为数组的一半）。 

也许使用索引搜索，例如Apache Lucene（http://lucene.apache.org/java/docs/index.html）提供将是一种更好的方法。 您可以自己尝试这项练习。

####    Text

文本文件（.txt，.html，.xml等）不需要第三方库。 

可以使用纯NIO.2读取它们代码如下：

```Java
boolean searchInText(Path file) {
    boolean flag = false;
    Charset charset = Charset.forName("UTF-8");

    try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
        String line = null;
        OUTERMOST:
        while ((line = reader.readLine()) != null) {
            flag = searchText(line);
            if (flag) {
                break OUTERMOST;
            }
        }

    } catch (IOException e) {
    } finally {
        return flag;
    }
}
```

####    编写完整的搜索程序

是! 馅饼准备好了！ 只是把它扔进烤箱！ 

我们有搜索到的文本，该文本是从一组常用的文件格式，以及一种检查提取的文本是否包含搜索到的文本的方法。 

遍历过程中的所有内容以及应用程序已准备就绪：

```Java
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.model.Notes;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.record.TextHeaderAtom;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

class Search implements FileVisitor {

    ArrayList<String> wordsarray = new ArrayList<>();
    ArrayList<String> documents = new ArrayList<>();
    boolean found = false;

    public Search(String words) {
        wordsarray.clear();
        documents.clear();
        StringTokenizer st = new StringTokenizer(words, ",");

        while (st.hasMoreTokens()) {
            wordsarray.add(st.nextToken().trim());
        }
    }

    void search(Path file) throws IOException {
        found = false;
        String name = file.getFileName().toString();
        int mid = name.lastIndexOf(".");
        String ext = name.substring(mid + 1, name.length());

        if (ext.equalsIgnoreCase("pdf")) {
            found = searchInPDF_iText(file.toString());
            if (!found) {
                found = searchInPDF_PDFBox(file.toString());
            }
        }

        if (ext.equalsIgnoreCase("doc") || ext.equalsIgnoreCase("docx")) {
            found = searchInWord(file.toString());
        }

        if (ext.equalsIgnoreCase("ppt")) {
            searchInPPT(file.toString());
        }

        if (ext.equalsIgnoreCase("xls")) {
            searchInExcel(file.toString());
        }

        if ((ext.equalsIgnoreCase("txt")) || (ext.equalsIgnoreCase("xml") || ext.equalsIgnoreCase("html")) || ext.equalsIgnoreCase("htm") || ext.equalsIgnoreCase("xhtml") || ext.equalsIgnoreCase("rtf")) {
            searchInText(file);
        }

        if (found) {
            documents.add(file.toString());
        }
    }


    //search in text files
    boolean searchInText(Path file) {
        boolean flag = false;
        Charset charset = Charset.forName("UTF-8");

        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            String line = null;
            OUTERMOST:
            while ((line = reader.readLine()) != null) {
                flag = searchText(line);
                if (flag) {
                    break OUTERMOST;
                }
            }
        } catch (IOException e) {
        } finally {
            return flag;
        }
    }

    //search in Excel files
    boolean searchInExcel(String file) {
        Row row;
        Cell cell;
        String text;
        boolean flag = false;
        InputStream xls = null;

    try {
        xls = new FileInputStream(file);
        HSSFWorkbook wb = new HSSFWorkbook(xls);
        int sheets = wb.getNumberOfSheets();
        OUTERMOST:

        for (int i = 0; i < sheets; i++) {
            HSSFSheet sheet = wb.getSheetAt(i);
            Iterator<Row> row_iterator = sheet.rowIterator();

            while (row_iterator.hasNext()) {
                row = (Row) row_iterator.next();
                Iterator<Cell> cell_iterator = row.cellIterator();

                while (cell_iterator.hasNext()) {
                    cell = cell_iterator.next();
                    int type = cell.getCellType();

                    if (type == HSSFCell.CELL_TYPE_STRING) {
                        text = cell.getStringCellValue();
                        flag = searchText(text);
                        if (flag) {
                            break OUTERMOST;
                        }
                    }
                }
            }
        }

        } catch (IOException e) {
            //...
        } finally {
            try {
                if (xls != null) {
                    xls.close();
                }
            } catch (IOException e) {
            }
                return flag;
        }
    }


    //search in PowerPoint files
    boolean searchInPPT(String file) {
        boolean flag = false;
        InputStream fis = null;
        String text;

        try {
            fis = new FileInputStream(new File(file));
            POIFSFileSystem fs = new POIFSFileSystem(fis);
            HSLFSlideShow show = new HSLFSlideShow(fs);
            SlideShow ss = new SlideShow(show);
            Slide[] slides = ss.getSlides();
            OUTERMOST:

            for (int i = 0; i < slides.length; i++) {
                TextRun[] runs = slides[i].getTextRuns();

                for (int j = 0; j < runs.length; j++) {
                    TextRun run = runs[j];
                    if (run.getRunType() == TextHeaderAtom.TITLE_TYPE) {
                        text = run.getText();
                    } else {
                        text = run.getRunType() + " " + run.getText();
                    }

                    flag = searchText(text);
                    if (flag) {
                        break OUTERMOST;
                    }
                }
                Notes notes = slides[i].getNotesSheet();
                if (notes != null) {
                    runs = notes.getTextRuns();

                    for (int j = 0; j < runs.length; j++) {
                        text = runs[j].getText();
                        flag = searchText(text);
                        if (flag) {
                            break OUTERMOST;
                        }
                    }
                }
            }
        } catch (IOException e) {
        } finally {
            try {
            if (fis != null) {
                fis.close();
            }
            } catch (IOException e) {
            }
            return flag;
        }
    }

    //search in Word files
    boolean searchInWord(String file) {
        POIFSFileSystem fs = null;
        boolean flag = false;

        try {
            fs = new POIFSFileSystem(new FileInputStream(file));
            HWPFDocument doc = new HWPFDocument(fs);
            WordExtractor we = new WordExtractor(doc);
            String[] paragraphs = we.getParagraphText();
            OUTERMOST:

            for (int i = 0; i < paragraphs.length; i++) {
                flag = searchText(paragraphs[i]);
                if (flag) {
                    break OUTERMOST;
                }
            }
        } catch (Exception e) {
        } finally {
            return flag;
        }
    }

    //search in PDF files using PDFBox library
    boolean searchInPDF_PDFBox(String file) {
        PDFParser parser = null;
        String parsedText = null;
        PDFTextStripper pdfStripper = null;
        PDDocument pdDoc = null;
        COSDocument cosDoc = null;
        boolean flag = false;
        int page = 0;
        File pdf = new File(file);

        try {
            parser = new PDFParser(new FileInputStream(pdf));
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            OUTERMOST:

            while (page < pdDoc.getNumberOfPages()) {
                page++;
                pdfStripper.setStartPage(page);
                pdfStripper.setEndPage(page + 1);
                parsedText = pdfStripper.getText(pdDoc);
                flag = searchText(parsedText);
                if (flag) {
                    break OUTERMOST;
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                if (cosDoc != null) {
                    cosDoc.close();
                }

                if (pdDoc != null) {
                    pdDoc.close();
                }
            } catch (Exception e) {
            }
            return flag;
        }
    }

    //search in PDF files using iText library
    boolean searchInPDF_iText(String file) {
        PdfReader reader = null;
        boolean flag = false;
        try {
            reader = new PdfReader(file);
            int n = reader.getNumberOfPages();
            OUTERMOST:
            for (int i = 1; i <= n; i++) {
                String str = PdfTextExtractor.getTextFromPage(reader, i);
                flag = searchText(str);
                if (flag) {
                    break OUTERMOST;
                }
            }
        } catch (Exception e) {
        } finally {
            if (reader != null) {
                reader.close();
            }
        return flag;
        }
    }

    //search text
    private boolean searchText(String text) {
        boolean flag = false;
        for (int j = 0; j < wordsarray.size(); j++) {
            if ((text.toLowerCase()).contains(wordsarray.get(j).toLowerCase())) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException exc) throws IOException {
        System.out.println("Visited: " + (Path) dir);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }
    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
        search((Path) file);
        return FileVisitResult.CONTINUE;
    }
    @Override
    public FileVisitResult visitFileFailed(Object file, IOException exc) throws IOException {
        //report an error if necessary
        return FileVisitResult.CONTINUE;
    }
}

class Main {
    public static void main(String[] args) throws IOException {
        String words = "Rafael Nadal, tennis, winner of Roland Garros, BNP Paribas tournament draws";
        Search walk = new Search(words);
        EnumSet opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        Iterable<Path> dirs = FileSystems.getDefault().getRootDirectories();

        for (Path root : dirs) {
            Files.walkFileTree(root, opts, Integer.MAX_VALUE, walk);
        }

        System.out.println("____________________________________________________________");
        for(String path_string: walk.documents){
            System.out.println(path_string);
        }

        System.out.println("____________________________________________________________");
    }
}

```

请注意，有时这是一个非常缓慢的过程，可能需要几秒钟到几十个小时分钟-运行时间将根据文件树的大小，已检查文件的数量以及
这些文件。 

在前面的示例中，文件树包含默认文件系统中的所有文件存储，因此将以我们支持的搜索词打开，阅读和浏览任何受支持格式的每个文件。

根据匹配文件的大小，该进程可能会卡在结果返回几秒钟。 

您可以通过添加更多文件格式来改进此应用程序，进度条或标志指示进程状态，以及多个线程以加快进程。 

此外，显示找到的文件名可能比存储文件名更好，

##  删除

如第4章“删除文件和文件”中所见，删除单个文件是一个简单的操作。

调用delete（）或deleteIfExists（）方法后，该文件将从文件中删除。

删除整个文件树是基于调用delete（）或deleteIfExists（）的操作方法通过FileVisitor实现递归地进行。 

在您看到示例之前，这里有一些您需要记住的事情：

-   删除目录之前，必须从其中删除所有文件。
-   visitFile（）方法是执行每个文件删除的最佳位置。
-   由于仅在目录为空时才可以删除它，因此建议删除postVisitDirectory（）方法中的目录。
-   如果无法访问文件，则visitFileFailed（）方法应返回 FileVisitResult.CONTINUE或TERMINATE，取决于您的决定。
-   删除过程可以遵循符号链接，因此不建议这样做，因为符号链接可能指向删除域之外的文件。 但是如果你确定这种情况永远不会发生，或者补充条件阻止了不需要的删除，然后点击符号链接。

本部分的目的是创建一个删除整个文件树的应用程序。 

以下代码删除C：\ rafaelnadal目录（为进一步使用，请在运行以下代码）：

```Java
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

class DeleteDirectory implements FileVisitor {
    boolean deleteFileByFile(Path file) throws IOException {
        return Files.deleteIfExists(file);
    }

    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException exc) throws IOException {
        if (exc == null) {
            System.out.println("Visited: " + (Path) dir);
            boolean success = deleteFileByFile((Path) dir);
            if (success) {
                System.out.println("Deleted: " + (Path) dir);
            } else {
                System.out.println("Not deleted: " + (Path) dir);
            }
        } else {
            throw exc;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
        boolean success = deleteFileByFile((Path) file);

        if (success) {
            System.out.println("Deleted: " + (Path) file);
        } else {
            System.out.println("Not deleted: " + (Path) file);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Object file, IOException exc) throws IOException {
        //report an error if necessary
        return FileVisitResult.CONTINUE;
    }
}

class Main {
    public static void main(String[] args) throws IOException {
        Path directory = Paths.get("C:/rafaelnadal");
        DeleteDirectory walk = new DeleteDirectory();
        EnumSet opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        Files.walkFileTree(directory, opts, Integer.MAX_VALUE, walk);
    }
}

```

将删除的文件发送到回收站可以通过使用JNI调用Windows API来完成SHFileOperation（）方法。 

在以下网址查看David Shay的帖子：www.jroller.com/ethdsy/entry/send_to_recycle_bin 更多细节。

##  复制

复制文件树需要为每个遍历的文件和目录调用Files.copy（）方法。 （对于有关在NIO.2中复制文件或目录的详细信息，请参阅第4章“复制文件和目录”目录。”）

在看到示例之前，这里需要牢记一些提示：

-   从目录复制任何文件之前，必须复制目录本身。复制源目录（空目录或非空目录）将导致目标目录为空。此任务必须在preVisitDirectory（）方法中完成。
-   visitFile（）方法是复制每个文件的理想场所。
-   复制文件或目录时，需要确定是否要使用REPLACE_EXISTING和COPY_ATTRIBUTES选项。
-   如果要保留源目录的属性，则需要这样做复制文件后，在postVisitDirectory（）方法中。
-  如果您选择跟随链接（FOLLOW_LINKS），并且文件树具有指向的循环链接父目录，则在visitFileFailed（）中报告循环目录FileSystemLoopException异常的方法。
-   如果无法访问文件，则visitFileFailed（）方法应返回FileVisitResult.CONTINUE或TERMINATE，取决于您的决定。
-   如果指定FOLLOW_LINKS选项，则复制过程可以跟随符号链接。

以下代码段合并了上述概念并复制了C：\ rafaelnadal C：\ rafaelnadal_copy文件树的子树：

```Java
import java.nio.file.FileSystemLoopException;
import java.nio.file.attribute.FileTime;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;

class CopyTree implements FileVisitor {
    private final Path copyFrom;
    private final Path copyTo;

    public CopyTree(Path copyFrom, Path copyTo) {
        this.copyFrom = copyFrom;
        this.copyTo = copyTo;
    }

    static void copySubTree(Path copyFrom, Path copyTo) throws IOException {
        try {
            Files.copy(copyFrom, copyTo, REPLACE_EXISTING, COPY_ATTRIBUTES);
        } catch (IOException e) {
            System.err.println("Unable to copy " + copyFrom + " [" + e + "]");
        }
    }

    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException exc) throws IOException {
        if (exc == null) {
            Path newdir = copyTo.resolve(copyFrom.relativize((Path) dir));
            try {
                FileTime time = Files.getLastModifiedTime((Path) dir);
                Files.setLastModifiedTime(newdir, time);
            } catch (IOException e) {
                System.err.println("Unable to copy all attributes to: " + newdir+" ["+e+ "]");
            }
        } else {
            throw exc;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
        System.out.println("Copy directory: " + (Path) dir);
        Path newdir = copyTo.resolve(copyFrom.relativize((Path) dir));

        try {
            Files.copy((Path) dir, newdir, REPLACE_EXISTING, COPY_ATTRIBUTES);
        } catch (IOException e) {
            System.err.println("Unable to create " + newdir + " [" + e + "]");
            return FileVisitResult.SKIP_SUBTREE;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
        System.out.println("Copy file: " + (Path) file);
        copySubTree((Path) file, copyTo.resolve(copyFrom.relativize((Path) file)));
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Object file, IOException exc) throws IOException {
        if (exc instanceof FileSystemLoopException) {
            System.err.println("Cycle was detected: " + (Path) file);
        } else {
            System.err.println("Error occurred, unable to copy:" +(Path) file+" ["+ exc + "]");
        }
        return FileVisitResult.CONTINUE;
    }
}

class Main {
    public static void main(String[] args) throws IOException {
        Path copyFrom = Paths.get("C:/rafaelnadal");
        Path copyTo = Paths.get("C:/rafaelnadal_copy");
        CopyTree walk = new CopyTree(copyFrom, copyTo);
        EnumSet opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        Files.walkFileTree(copyFrom, opts, Integer.MAX_VALUE, walk);
    }
}

```

运行上述应用程序后，您将找到一个C：\ rafaelnadal_copy目标，该目标具有 与C：\ rafaelnadal源具有相同的内容和属性。

##  移动

移动文件树是一项将复制和删除文件的步骤合并到一个应用程序中的任务。文件树。 （有关移动文件的更多详细信息，请参阅第4章“移动文件和目录。”

实际上，通常有两种方法来移动文件树：合并 Files.move（），Files.copy（）和Files.delete（），或仅使用Files.copy（）和Files.delete（）。

根据您选择的方法，应相应地实现FileVisitor完成移动文件树任务。 

在看到示例之前，您需要保留以下一些内容心神：

-   从目录中移动任何文件之前，必须移动目录本身。由于非空目录不能移动（只能空目录移动），则需要使用Files.copy（）方法，该方法将复制一个空白目录代替。 此任务必须在preVisitDirectory（）中完成方法。
-   visitFile（）方法是移动每个文件的理想场所。 为此，您可以使用Files.move（）方法，或将Files.copy（）与Files.delete（）结合使用。
-   将源目录中的所有文件移至目标目录后，您需要调用Files.delete（）删除源目录，此刻，应该为空。 此任务必须在postVisitDirectory（）中完成方法。
-   复制文件或目录时，需要确定是否要使用REPLACE_EXISTING和COPY_ATTRIBUTES选项。 而且，当你移动文件或目录，您需要确定是否需要ATOMIC_MOVE。
-   如果要保留源目录的属性，则需要这样做文件移动后，在postVisitDirectory（）方法中。 一些
属性，例如lastModifiedTime，应在preVisitDirectory（）方法并存储，直到将它们设置为postVisitDirectory（）。 原因是从源中移出文件后目录，目录内容已更改，初始上次修改时间为被新日期覆盖。
-   如果无法访问文件，则visitFileFailed（）方法应返回FileVisitResult.CONTINUE或TERMINATE，取决于您的决定
-   如果您指定FOLLOW_LINKS，则移动过程可以跟随符号链接选项。 请记住，移动符号链接会移动链接本身，而不是链接本身。该链接的目标。

以下代码段将C：\ rafaelnadal目录内容移至 C：\ ATP \ players \ rafaelnafal目录（测试之前，必须手动创建文件夹 C：\ ATP \ players \）。 

在这种情况下，目录和子目录使用Files.copy（）和 Files.delete（），然后使用Files.move（）移动文件。

```Java
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.EnumSet;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

class MoveTree implements FileVisitor {
    private final Path moveFrom;
    private final Path moveTo;
    static FileTime time = null;

    public MoveTree(Path moveFrom, Path moveTo) {
        this.moveFrom = moveFrom;
        this.moveTo = moveTo;
    }

    static void moveSubTree(Path moveFrom, Path moveTo) throws IOException {
        try {
            Files.move(moveFrom, moveTo, REPLACE_EXISTING, ATOMIC_MOVE);
        } catch (IOException e) {
            System.err.println("Unable to move " + moveFrom + " [" + e + "]");
        }
    }

    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException exc) throws IOException {
        Path newdir = moveTo.resolve(moveFrom.relativize((Path) dir));
        try {
            Files.setLastModifiedTime(newdir, time);
            Files.delete((Path) dir);
        } catch (IOException e) {
            System.err.println("Unable to copy all attributes to: " + newdir+" [" + e + "]");
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
        System.out.println("Move directory: " + (Path) dir);
        Path newdir = moveTo.resolve(moveFrom.relativize((Path) dir));
        try {
            Files.copy((Path) dir, newdir, REPLACE_EXISTING, COPY_ATTRIBUTES);
            time = Files.getLastModifiedTime((Path) dir);
        } catch (IOException e) {
            System.err.println("Unable to move " + newdir + " [" + e + "]");
            return FileVisitResult.SKIP_SUBTREE;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
        System.out.println("Move file: " + (Path) file);
        moveSubTree((Path) file, moveTo.resolve(moveFrom.relativize((Path) file)));
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Object file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}

class Main {
    public static void main(String[] args) throws IOException {
        Path moveFrom = Paths.get("C:/rafaelnadal");
        Path moveTo = Paths.get("C:/ATP/players/rafaelnadal");
        MoveTree walk = new MoveTree(moveFrom, moveTo);
        EnumSet opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        Files.walkFileTree(moveFrom, opts, Integer.MAX_VALUE, walk);
    }
}
```

您无需使用Files.move（）就可以完成相同的任务，因为每一步都是复制和删除操作。 

例如，您可以重写moveSubTree（）方法以使用Files.copy（）和Files.delete（）也可以移动文件：

```Java
static void moveSubTree(Path moveFrom, Path moveTo) throws IOException {
    try {
        Files.copy(moveFrom, moveTo, REPLACE_EXISTING, COPY_ATTRIBUTES);
        Files.delete(moveFrom);
    } catch (IOException e) {
        System.err.println("Unable to move " + moveFrom + " [" + e + "]");
    }
}
```

----