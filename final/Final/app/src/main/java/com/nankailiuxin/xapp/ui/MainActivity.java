package com.nankailiuxin.xapp.ui;

import android.animation.LayoutTransition;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nankailiuxin.xapp.R;
import com.nankailiuxin.xapp.adapter.MyNoteListAdapter;
import com.nankailiuxin.xapp.bean.Note;
import com.nankailiuxin.xapp.db.NoteDao;
import com.nankailiuxin.xapp.util.CommonUtil;
import com.nankailiuxin.xapp.view.SpacesItemDecoration;

import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity {
    private MyNoteListAdapter mNoteListAdapter;
    private List<Note> noteList;
    private NoteDao noteDao;

    private SearchView mSearchView;
    private SearchView.SearchAutoComplete mSearchAutoComplete;

    public String URL = "https://hello-cloudbase-7gv2as219657f1f3-1305336850.tcloudbaseapp.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermissions(this);
        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewActivity.class);
                intent.putExtra("groupName", "默认笔记");
                intent.putExtra("flag", 0);
                startActivity(intent);
            }
        });

        noteDao = new NoteDao(this);

        RecyclerView rv_list_main = findViewById(R.id.rv_list_main);
        rv_list_main.addItemDecoration(new SpacesItemDecoration(0));

//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        rv_list_main.setLayoutManager(layoutManager);

        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager
                (gridColumnCount, StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setReverseLayout(false);
        rv_list_main.setLayoutManager(staggeredGridLayoutManager);

        mNoteListAdapter = new MyNoteListAdapter();
        mNoteListAdapter.setmNotes(noteList);
        rv_list_main.setAdapter(mNoteListAdapter);

        mNoteListAdapter.setOnItemClickListener(new MyNoteListAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Note note) {
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("note", note);
                intent.putExtra("data", bundle);
                startActivity(intent);
            }
        });

        mNoteListAdapter.setOnItemLongClickListener(new MyNoteListAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final Note note) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("提示");
                builder.setMessage("确定删除美食记录？");
                builder.setCancelable(false);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int ret = noteDao.deleteNote(note.getId());
                        if (ret > 0){
                            showToast("删除成功");
                            refreshNoteList();
                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
            }
        });
    }

    private void refreshNoteList() {
        if (noteDao == null) {
            noteDao = new NoteDao(this);
        }

        if (noteDao.queryNotesAll(0).size() == 0) {
            Note note23 = new Note();
            note23.setTitle(getResources().getString(R.string.Macaron));
            note23.setContent("<img src=\"" + URL + "/photos/%E9%A9%AC%E5%8D%A1%E9%BE%99.jpg\" />" +
                    "\n马卡龙作为一种法式甜点为人们所熟知，但实际上，马卡龙是意大利人发明的。\n" +
                    "\n马卡龙刚传到法国时，与如今的马卡龙还是有很大区别的：只是单片，没有夹心。蛋白杏仁饼传到法国后，特别是进入19世纪后，大批法国厨师竞相制作这种甜品，单片渐渐变成夹心；同时，富有想象力的法国大厨们也尝试加入不同的水果和果酱，——甚至是咖啡、巧克力来创造五彩缤纷的颜色。就这样，杏仁饼变成了21世纪的马卡龙。\n" +
                    "\n像这样，在法国发扬光大的马卡龙，被贴上了浓浓的法式标签。因此，马卡龙不单单是一种甜点、一种美食，它更承载着一种文化。\n" +
                    "\n马卡龙历史上曾是贵族食物，是奢华的象征。但随着历史的发展，马卡龙渐渐进入寻常百姓家，以其缤纷的色彩、清新细腻的口感和小巧玲珑的造型，博得人们，特别是女生们的喜爱。\n");
            note23.setGroupId(0);
            note23.setGroupName("默认笔记");
            note23.setType(2);
            note23.setBgColor("#FFFFFF");
            note23.setIsEncrypt(0);
            note23.setCreateTime(CommonUtil.date2string(new Date()));
            noteDao.insertNote(note23);

            Note note24 = new Note();
            note24.setTitle(getResources().getString(R.string.Egg));
            note24.setContent("<img src=\"" + URL + "/photos/%E9%B8%A1%E8%9B%8B.jpg\" />" +
                    "\n鸡蛋，是一种常见的食品，多作为早餐食用，因为烹饪简单便捷、美味营养而成为大众化的食品。煎蛋可以随着人们口味的变化而在烹饪上变换更多的花样，其营养价值也因加入的配餐食品而得到了进一步的提升。\n");
            note24.setGroupId(0);
            note24.setGroupName("默认笔记");
            note24.setType(2);
            note24.setBgColor("#FFFFFF");
            note24.setIsEncrypt(0);
            note24.setCreateTime(CommonUtil.date2string(new Date()));
            noteDao.insertNote(note24);

            Note note1 = new Note();
            note1.setTitle(getResources().getString(R.string.Sandwich));
            note1.setContent("<img src=\"" + URL + "/photos/%E4%B8%89%E6%98%8E%E6%B2%BB.jpg\" />" +
                    "\n三明治的历史几乎和蛋糕一样古老，只是最初似乎没有一个特定的名字而已，关于sandwich的来历，还有一个故事。\n" +
                    "\nsandwich本来是英国东南部一个不出名的小镇，镇上有一位Earl of Sandwich名叫John Montagu，他是个酷爱玩纸牌的人，他整天沉溺于纸牌游戏中，已经到了废寝忘食的地步。仆人很难侍候他的饮食，便将一些菜肴、鸡蛋和腊肠夹在两片面包之间，让他边玩牌边吃饭。\n" +
                    "\n没想到Montagu见了这种食品大喜，并随口就把它称作“sandwich”，以后饿了就喊：“拿sandwich来！”其他赌徒也争相仿效，玩牌时都吃起sandwich来。不久，sandwich就传遍了英伦三岛，并传到了欧洲大陆，后来又传到了美国。\n" +
                    "\n如今的sandwich已不再像当初那样品种单一，它已经发展了许多新品种。例如，有夹鸡或火鸡肉片、咸肉、莴苣、番茄的“夜总会sandwich”，有夹咸牛肉、瑞士奶酪、泡菜并用俄式浇头盖在黑面包片上的“劳本sandwich”，有夹鱼酱、黄瓜、水芹菜、西红柿的“饮茶专用sandwich”等等。在法国，制作sandwich时往往已不用面包片，而是改用面包卷或面卷。除此以外，以法国长棍制成的Sandwich还被称为“潜艇包”。\n" +
                    "\n在西方所有食谱中，任何一种面包或面卷，任何一种便于食用的食品，都可制成三明治。在美国，小面包夹牛肉的“热三明治” 是最常见的营养快餐；夹花生酱和果冻的三明治，是小学生的主要食品；夹鸡或火鸡肉片、咸肉、莴苣、番茄的“夜总会三明治”，夹腌牛肉、瑞士奶 酪、泡菜，并用俄式浇头盖在黑面包上烤热食用的“劳本三明治”，价格较贵，主要在高档饭店和舞厅出售。在英国，用鱼酱、黄瓜、水芹菜、西红柿夹在薄面片中的三明治，是饮茶专用的。在北欧斯堪的那维亚诸国，三明治已无上面一层，而以精致的鱼、肉片和沙拉覆盖。\n");
            note1.setGroupId(0);
            note1.setGroupName("默认笔记");
            note1.setType(2);
            note1.setBgColor("#FFFFFF");
            note1.setIsEncrypt(0);
            note1.setCreateTime(CommonUtil.date2string(new Date()));
            noteDao.insertNote(note1);

            Note note3 = new Note();
            note3.setTitle(getResources().getString(R.string.Shakshuka));
            note3.setContent("<img src=\"" + URL + "/photos/%E5%8C%97%E9%9D%9E%E8%9B%8B.jpg\" />" +
                    "\n北非蛋的真实名字叫做 Shakshuka， 来自中东地区和北非，中文给了它取了这么个可爱的名字 —— 北非蛋。\n" +
                    "\nShakshuka 在阿拉伯语中的直译就是“混合物”的意思，简单明了直入主题的说出了它的做法。就是将西红柿、洋葱、大蒜和一些香料混合来烹饪鸡蛋，是一道营养十足，健康美味，并且还很适合素食者的佳肴。\n" +
                    "\n欧美国家很多brunch餐厅也会出现这道菜，蔬菜和鸡蛋的完美结合，还可以搭配烤面包吃，或者拌上芝士和意面。\n");
            note3.setGroupId(0);
            note3.setGroupName("默认笔记");
            note3.setType(2);
            note3.setBgColor("#FFFFFF");
            note3.setIsEncrypt(0);
            note3.setCreateTime(CommonUtil.date2string(new Date()));
            noteDao.insertNote(note3);

            Note note2 = new Note();
            note2.setTitle(getResources().getString(R.string.Ice_cream));
            note2.setContent("<img src=\"" + URL + "/photos/%E5%86%B0%E6%B7%87%E6%B7%8B.jpg\" />" +
                    "\n冰淇淋是一种极具诱惑力的美味冷冻奶制品。\n" +
                    "\n1500年，法国一位国王与意大利皇室的一位成员结婚时，冰淇淋又由意大利传入了法国，法国人在原有做法的基础上，又增加了许多新的配料，1625年，新继位的英国国王查理一世，为能吃到这种消暑食品，曾专门聘请了一位厨师来制作冰淇淋，并要求这位厨师对冰淇淋的配方严加保密。\n" +
                    "\n大约在1700年，冰淇淋传入美洲大陆，美国首任总统乔治·华盛顿对这种新工艺痴爱异常。当时，冰淇淋的制作还很不容易，人们要在夏天吃到这种食品，不得不在冬天到河里取冰块，把它们贮放在锯末里。冰淇淋仍然是富贵人家的食品。1846年，美国的一位名叫南希·约翰逊的女士对复杂的工艺进行了改进，制造了一种手动曲柄式冷冻机，使冰淇淋的制作工艺更加简单容易。1851年，美国人扎卡布·费斯赛尔在美国马里兰州的巴尔的摩开办了美国首家冰淇淋制作工厂。\n" +
                    "\n1900年，由于电力和热力学理论的广泛应用，冰淇淋的制作过程加快，降低了成本，从而使价格大大下降。从那以后，冰淇淋就开始成为一种普及的降暑食品。\n" +
                    "\n由于冰箱和冰柜在中国的日益普及，每逢炎热的夏季来临，无论城市还是乡村的大小商店，都有冰淇淋出售，许多家庭还习惯自制适合自己口味的冰淇淋，即使在寒冷的冬天，也有不少人喜欢吃冰淇淋。\n");
            note2.setGroupId(0);
            note2.setGroupName("默认笔记");
            note2.setType(2);
            note2.setBgColor("#FFFFFF");
            note2.setIsEncrypt(0);
            note2.setCreateTime(CommonUtil.date2string(new Date()));
            noteDao.insertNote(note2);

//            Note note4 = new Note();
//            note4.setTitle("南瓜汤");
//            note4.setContent("<img src=\"https://hello-cloudbase-7gv2as219657f1f3-1305336850.tcloudbaseapp.com/photos/%E5%8D%97%E7%93%9C%E6%B1%A4.jpg\" />");
//            note4.setGroupId(0);
//            note4.setGroupName("默认笔记");
//            note4.setType(2);
//            note4.setBgColor("#FFFFFF");
//            note4.setIsEncrypt(0);
//            note4.setCreateTime(CommonUtil.date2string(new Date()));
//            noteDao.insertNote(note4);

            Note note5 = new Note();
            note5.setTitle(getResources().getString(R.string.Coffee));
            note5.setContent("<img src=\"" + URL + "/photos/%E5%92%96%E5%95%A1.jpg\" />" +
                    "\n“咖啡”一词源自阿拉伯语“قهوة”，意思是“植物饮料”。在世界各地，人们越来越爱喝咖啡。随之而来的“咖啡文化”充满生活的每个时刻。无论在家里、还是在办公室、或是各种社交场合，人们都在品着咖啡、它逐渐与时尚、现代生活、工作和休闲娱乐联系在一起。\n" +
                    "\n咖啡树原产于非洲埃塞俄比亚西南部的高原地区。据说一千多年以前一位牧羊人发现羊吃了一种植物后，变得非常兴奋活泼，进而发现了咖啡。还有说法称是因野火偶然烧毁了一片咖啡林，烧烤咖啡的香味引起周围居民注意。\n" +
                    "\n当地土著人经常把咖啡树的果实磨碎，再把它与动物脂肪掺在一起揉捏，做成许多球状的丸子。这些土著部落的人将这些咖啡丸子当成珍贵的食物，专供那些即将出征的战士享用。直到11世纪左右，人们才开始用水煮咖啡作为饮料。13世纪时，埃塞俄比亚军队入侵也门，将咖啡带到了阿拉伯世界。因为伊斯兰教义禁止教徒饮酒，有的宗教界人士认为这种饮料刺激神经，违反教义，曾一度禁止并关闭咖啡店，但埃及苏丹认为咖啡不违反教义，因而解禁，咖啡饮料迅速在阿拉伯地区流行开来。咖啡Coffee这个词，就是来源于阿拉伯语“قهوة（qahwa）”，意思是“植物饮料”，后来传到土耳其，成为欧洲语言中这个词的来源。咖啡种植，制作的方法也被阿拉伯人不断地改进而逐渐完善。\n" +
                    "\n在公元15世纪以前，咖啡的种植和生产一直为阿拉伯人所垄断。当时主要被使用在医学和宗教上，医生和僧侣们承认咖啡具有提神、醒脑、健胃、强身、止血等功效；15世纪初开始有文献记载咖啡的使用方式，并且在此时期融入宗教仪式中，同时也在民间作为日常饮品。因伊斯兰教严禁饮酒，因此咖啡成为当时很重要的社交饮品。\n" +
                    "\n1683年，土耳其军队围攻维也纳，失败撤退时，有人在土耳其军队的营房中发现一口袋黑色的种子，谁也不知道是什么东西。一个曾在土耳其生活过的波兰人，拿走了这袋咖啡，在维也纳开了第一家咖啡店。\n" +
                    "\n16世纪末，咖啡以“伊斯兰酒”的名义通过威尼斯商人和海上霸权荷兰人的买卖辗转将咖啡传入欧洲，很快地，这种充满东方神秘色彩、口感馥郁香气迷魅的黑色饮料受到贵族士绅阶级的争相竞逐，咖啡的身价也跟着水涨船高，甚至产生了“黑色金子”的称号。当时的贵族流行在特殊日子互送咖啡豆以示尽情狂欢，或是给久未谋面的亲友，有财入袋、祝贺顺遂之意，同时也是身份地位象征。而“黑色金子”在接下来风起云涌的大航海时代，借由海运的传播，全世界都被纳入了咖啡的生产和消费版图中。\n" +
                    "\n相传1600年时一些天主教人士认为咖啡是“魔鬼饮料”，怂恿当时的教皇克莱门八世禁止这种饮料，但教皇品尝后认为可以饮用，并且祝福了咖啡，因此咖啡在欧洲逐步普及。\n" +
                    "\n直到1690年，一位荷兰船长航行到也门，得到几棵咖啡苗，在印度尼西亚种植成功。1727年荷属圭亚那的一位外交官的妻子，将几粒咖啡种子送给一位在巴西的西班牙人，他在巴西试种取得很好的效果。巴西的气候非常适宜咖啡生长，从此咖啡在南美洲迅速蔓延。因大量生产而价格下降的咖啡开始成为欧洲人的重要饮料。\n");
            note5.setGroupId(0);
            note5.setGroupName("默认笔记");
            note5.setType(2);
            note5.setBgColor("#FFFFFF");
            note5.setIsEncrypt(0);
            note5.setCreateTime(CommonUtil.date2string(new Date()));
            noteDao.insertNote(note5);

            Note note6 = new Note();
            note6.setTitle(getResources().getString(R.string.Kulry));
            note6.setContent("<img src=\"" + URL + "/photos/%E5%92%96%E5%96%B1.jpg\" />" +
                    "\n咖喱（kulry）的专有名词是从“kari”演化而来的，印地语叫kuri，在印度使用英语的地区（即印度南部），kulry是一被英语化的拼法，在南印度Tamil Nadu省KULRY则被拼为KALRI，由肉汁或酱汁且多搭配米饭、或是面包的一种主食。亦有另一说法，此字在十四世纪古英文就已存在于烹调文献中，被拼为KULY，是源自于法语kuile（意译为煮）。\n" +
                    "\n咖喱在泰米尔语（Tamil word）是指一种酱，是在南印度的多种菜肴的综合，用蔬菜或肉类做成且经常与米饭一起食用。咖哩这个词已经被广泛的使用，特别是在西半球，几乎任何加有香料的，加有酱料的菜肴或带有南亚和东南亚洲风格的菜肴都叫做咖哩。这个不严密的伞形结构的词，主要是英国统治所留下来的东西。这是一般的误解，认为所有用咖哩粉末或是用各种肉类、蔬菜做成的菜肴都是咖哩。在印度，咖哩这个词事实上是很少用。大部分菜肴包含了豆类（lentils）的菜叫“dahl”，意指这是一种用做备用的香料。肉类或是蔬菜做成的菜肴同样地给予特定的名字，用以区别烹调的方法或是特殊的香料使用。然而北印度人和巴基斯坦人的菜肴的名字的确叫做咖哩（kulry or khadi）——这包含了酸奶酪（yoghurt）、酥油（ghee）、印度酥油。\n" +
                    "\n一位咖哩权威作家Blent Thompson对咖哩曾写下这么一段话：在印度kulry一词，并不是真正被使用如我们所认知的一般，除非你专指的是普遍存在于印度，但由英国人归类，把包含着姜、大蒜、洋葱、姜黄、辣椒及油所烹煮的汤或炖菜，其大多为黄色，红色，多油，味辛辣且浓郁。辣椒在印地语叫 mirch ，红的叫 lal ，绿的叫 hali ，只有红的用来煮咖喱，但煮出来的不只红色，还有黄、绿、橙、咖啡色等，大中小辣兼而有之。而今日咖哩则普遍被定义为由新鲜或干燥香料以油炒香，并加入洋葱泥、大蒜、姜一起熬煮。其中香料并没有一定限制，大多有辣椒，小茴香，香菜及姜黄等。\n");
            note6.setGroupId(0);
            note6.setGroupName("默认笔记");
            note6.setType(2);
            note6.setBgColor("#FFFFFF");
            note6.setIsEncrypt(0);
            note6.setCreateTime(CommonUtil.date2string(new Date()));
            noteDao.insertNote(note6);

            Note note7 = new Note();
            note7.setTitle(getResources().getString(R.string.Taco));
            note7.setContent("<img src=\"" + URL + "/photos/%E5%A1%94%E5%8F%AF.jpg\" />" +
                    "\nTaco塔可源自墨西哥风味玉米饼，也称为墨西哥卷。数百年来，玉米一直是墨西哥食品中的主角，而以玉米为原料制成的Tortia饼也是墨西哥最基本、也最有特色的食品，这是一张用玉米煎制的薄饼，吃的时候，顾客可根据自己的喜好加入碳烤的鸡肉条或是牛肉酱，然后再加入蕃茄、生菜丝、玉米饼起司等等配料，看上去颜色格外丰富，就好似一件艺术品一般。包好以后，放入嘴中一咬，外面脆生生的，而里面却有香、辣、酸、甜各味俱全，刚柔相济、多味混杂，这些统称塔可（taco）食品。\n" +
                    "\n塔可（taco）在美国广为流传，其实是一种墨西哥风味的玉米饼，有软塔可和硬塔可之分。软塔可：是由柔软、弹性的面皮中加入了蔬菜、豆泥、沙拉酱、肉等馅料包裹制成。用牙签固定；硬塔可：和软塔可一样唯一不一样的是他的面皮改成了玉米脆片，香脆可口，不需用牙签固定。\n" +
                    "\n它可以仅是一种休闲食品，它可做成较大的也可以做成较小的，是具有独特风味的理想食品。\n" +
                    "\n墨西哥卷在当地被称为“塔可”，这种用玉米饼卷菜的小食品如此风行墨西哥，以至于邻国美国把它当作了墨西哥餐的代表。不仅在美国有为数众多的墨西哥卷餐馆，就连肯德基中国地区的餐厅，也推出了墨西哥鸡肉卷（塔可）。\n");
            note7.setGroupId(0);
            note7.setGroupName("默认笔记");
            note7.setType(2);
            note7.setBgColor("#FFFFFF");
            note7.setIsEncrypt(0);
            note7.setCreateTime(CommonUtil.date2string(new Date()));
            noteDao.insertNote(note7);

            Note note8 = new Note();
            note8.setTitle(getResources().getString(R.string.Chocolate));
            note8.setContent("<img src=\"" + URL + "/photos/%E5%B7%A7%E5%85%8B%E5%8A%9B.jpg\" />" +
                    "\n巧克力（chocolate）最初来源于中美洲热带雨林中野生可可树的果实可可豆。1300多年前，约克坦玛雅印第安人用焙炒过的可可豆做了一种饮料叫chocolate。早期的chocolate是一种油腻的饮料，因为炒过的可可豆中含50%以上油脂，人们开始把面粉和其它淀粉物质加到饮料中来降低其油腻度。\n" +
                    "\n16世纪初的西班牙探险家荷南多·科尔特斯在墨西哥发现：当地的阿兹特克国王饮用一种可可豆加水和香料制成的饮料，科尔特斯品尝后在1528年带回西班牙，并在西非一个小岛上种植了可可树。\n" +
                    "\n西班牙人将可可豆磨成了粉，从中加入了水和糖，在加热后被制成的饮料称为“巧克力”，深受大众的欢迎。不久其制作方法被意大利人学会，并且很快传遍整个欧洲。\n" +
                    "\n1642年，巧克力被作为药品引入法国，由天主教人士食用。\n" +
                    "\n1765年，巧克力进入美国，被本杰明·富兰克林赞为“具有健康和营养的甜点”。\n" +
                    "\n1828年，荷兰Van HOUTEN制作了可可压榨机，以便从可可液中压榨出剩余的粉状物。由Van HOUTEN压榨出的可可油脂与碾碎的可可豆及白糖混合，世界上第一块巧克力就诞生了。经过发酵、干燥和焙炒之后的可可豆，加工成可可液块、可可脂和可可粉后会产生浓郁而独特的香味，这种天然香气正是构成巧克力的主题。\n" +
                    "\n1847年，巧克力饮料中被加入可可脂，制成如今人们熟知的可咀嚼巧克力块。\n" +
                    "\n1875年，瑞士发明了制造牛奶巧克力的方法，从而有了所看到的巧克力。\n" +
                    "\n1914年，第一次世界大战刺激了巧克力的生产，巧克力被运到战场分发给士兵。\n" +
                    "\n巧克力由多种原料混合而成，但其风味主要取决于可可本身的滋味。可可中含有可可碱和咖啡碱，带来令人愉快的苦味；可可中的单宁质有淡淡的涩味，可可脂能产生肥腴滑爽的味感。可可的苦、涩、酸，可可脂的滑，借助砂糖或乳粉、乳脂、麦芽、卵磷脂、香兰素等辅料，再经过精湛的加工工艺，使得巧克力不仅保持了可可独有的滋味并且让它更加和谐、愉悦和可口。\n");
            note8.setGroupId(0);
            note8.setGroupName("默认笔记");
            note8.setType(2);
            note8.setBgColor("#FFFFFF");
            note8.setIsEncrypt(0);
            note8.setCreateTime(CommonUtil.date2string(new Date()));
            noteDao.insertNote(note8);

            Note note9 = new Note();
            note9.setTitle(getResources().getString(R.string.Spaghetti));
            note9.setContent("<img src=\"" + URL + "/photos/%E6%84%8F%E5%A4%A7%E5%88%A9%E9%9D%A2.jpg\" />" +
                    "\n意大利面的世界就像是千变万化的万花筒，数量种类之多据说至少有500种，再配上酱汁的组合变化，可做出上千种的意大利面，是意大利的特色主食。\n" +
                    "\n意大利面的起源简单说有人主张起源于中国，由马可·波罗带回意大利，后传播到整个欧洲。\n" +
                    "\n也有人主张：当年，罗马帝国为了解决人口多、粮食不易保存的难题，想出了把面粉揉成团、擀成薄饼再切条晒干的妙计，从而发明了名垂千古的著名美食——PASTA(意大利面)。\n" +
                    "\n最早的意大利面约成型于公元13至14世纪，与21世纪我们所吃的意大利面最像。到文艺复兴时期后，意大利面的种类和酱汁也随着艺术逐渐丰富起来。\n" +
                    "\n最初的意大利面都是这样揉了切、切了晒，吃的时候和肉类、蔬菜一起放在焗炉里做，因此当年意大利半岛上许多城市的街道、广场，随处可见抻面条、晾面条的人。据说最长的面条竟有800米。不过由于意大利面最初是应付粮荒的产物，所以青睐者多是穷人，但其美味很快就让所有阶层无法抵挡。\n" +
                    "\n意大利面吃起来连汁带水，颇不方便。早期的人们都是用手指去抓，吃完后还意犹未尽地把蘸着汁水的十指舔净。\n" +
                    "\n中世纪时，一些上层人士觉得这样吃相不雅，绞尽脑汁发明了餐叉，可以把面条卷在四个叉齿上送进嘴里。餐叉的发明被认为是西方饮食进入文明时代的标志。从这个意义上讲，意大利面功不可没。\n" +
                    "\n新大陆的发现开拓了人们的想象力，也给意大利面带来更多变化：两种从美洲舶来的植物——辣椒和西红柿被引入酱料。\n" +
                    "\n西红柿的出现及随后的品种改良，在意大利的那波利首次被人用作酱汁搭配面条，从此令面条备受欢迎，甚至连皇室贵族也被受吸引。正宗的意大利粉是由铜造的模子压制而成，由于外型较粗厚而且凸不平，表面较容易黏上调味酱料，令吃起来的味道和口感更佳。\n" +
                    "\n到19世纪末，意大利面著名的三大酱料体系：番茄底、鲜奶油底和橄榄油底完全形成，配以各种海鲜、蔬菜、水果、香料，形成复杂多变的酱料口味。面条本身也变化纷呈，有细长、扁平、螺旋、蝴蝶等多种形状，并通过添加南瓜、菠菜、葡萄等制成五颜六色的种类。据统计，意大利面的品种竟有563种之多。可是谁会想到意大利面条最早是用脚揉面的？因为面团太大，用手实在揉不动。\n" +
                    "\n直到18世纪，讲卫生的那不勒斯国王费迪南多二世才请来巧匠，发明了揉面机。\n" +
                    "\n1740年，第一座面条工厂建成，广场晒面的大场面从此成为历史。意大利人对面条的喜爱似乎与生俱来，许多人把做面的独门秘方束之高阁，不肯轻易示人，甚至把意大利面郑重写进遗嘱。中世纪许多歌剧、小说里都提到面条。近代意大利民族英雄加里波第也曾用面条犒赏三军，甚至拿破仑在波河大进军中也拿“吃面”激励士气。\n" +
                    "\n21世纪，全球意大利面条年产量已达1000万吨。在意大利，每人每年要吃掉至少28公斤面条。在罗马市中心总统府附近，甚至还建有一座别具一格的面条博物馆，慕名前来者络绎不绝。这个博物馆共有11个展厅，展出了不同时期的面条产品以及加工器具，从最早的擀面杖、和面盆，到后来的切面机、面条生产线等等。众多实物生动地叙述了意大利面条数百年的发展历史。如今意大利面条已成为世界的宠儿。\n" +
                    "\n2013的世界面条大会，参加国多达27个。美国纽约著名的“7月4日大胃王”比赛中，意大利面条大赛已成为保留节目。2013年的比赛中9名参赛者在短短8分钟里吞掉16.2公斤面条，平均每人吃掉1.8公斤；今天在世界100多个国家里，都可以找到意大利面的踪影，甚至在地球之外也能闻到它的香气——国际空间站的食谱里，意大利面条赫然在列。\n");
            note9.setGroupId(0);
            note9.setGroupName("默认笔记");
            note9.setType(2);
            note9.setBgColor("#FFFFFF");
            note9.setIsEncrypt(0);
            note9.setCreateTime(CommonUtil.date2string(new Date()));
            noteDao.insertNote(note9);

            Note note10 = new Note();
            note10.setTitle(getResources().getString(R.string.Pizza));
            note10.setContent("<img src=\"" + URL + "/photos/%E6%8A%AB%E8%90%A8.jpg\" />" +
                    "\n“披萨”是一种由特殊的酱汁和馅料做成的具有意大利风味的食品，但其实这种食品已经超越语言与文化的障碍，成为全球通行的小吃，受到各国消费者的喜爱。\n" +
                    "\n这种美食起源于公元前三世纪，罗马历史记载：圆麦饼+橄榄油+香料+石上烤。\n" +
                    "\n比萨由面包而来，在西方历史里面包作为主食占着一个重要的位置。它在时间的长流里并不是一直不变，而是通过加入不同的食材为了更好的满足人们的需求和爱好。历史学家就在意大利撒丁岛发现了3000年前类似从面包到比萨的过渡食品。在古希腊也多次出现饼状面包加入各种香料，其中就有蒜和葱. 还有波斯人, 一个叫Dario il Grande 的国王(521-486 a.C.) 使用石头烤一种扁面包, 里面加入了奶酪。\n" +
                    "\n在西方诗歌Eneide中也出现过最早期的过渡物，它们一般去被称为\"focaccia\" （寓意用火烤过的），西方 的 \"coca\" (咸甜皆宜)，希腊和意大利语的 \"pita\" 或 土耳其的语\"pide\" ，还有罗马涅的 \"piadina\" . 然后还有其他国家也出现过类似的食品。时间慢慢流转，语言也不是一成不变意大利语的pita/pitta慢慢变成pizza，上面的食材也不再限于最初的那些。\n" +
                    "\n比萨的创新还是来源于番茄的加入（其实还有一种是鱼的，在番茄的位置上替换)，那不勒斯是第一个带来这个改变的，可以说是现代比萨的起源地。但是最终把比萨推向世界的还是一个那不勒斯厨师在1889年特意制作献给Savoia 女王的 玛格丽特比萨 (pizza Margherita) ， 用女王名字命名的三色比萨。据统计，意大利总共有两万多家匹萨店，其中那不勒斯地区就有1200家。大多数那不勒斯人每周至少吃一个匹萨，有些人几乎每天午餐和晚餐都吃。食客不论贫富，都习惯是将匹萨折起来，拿在手上吃。这便成为鉴定匹萨手工优劣的依据之一。匹萨必须软硬适中，即使将其如“皮夹似的”折叠起来，外层也不会破裂。\n");
            note10.setGroupId(0);
            note10.setGroupName("默认笔记");
            note10.setType(2);
            note10.setBgColor("#FFFFFF");
            note10.setIsEncrypt(0);
            note10.setCreateTime(CommonUtil.date2string(new Date()));
            noteDao.insertNote(note10);

//            Note note11 = new Note();
//            note11.setTitle("松饼");
//            note11.setContent("<img src=\"https://hello-cloudbase-7gv2as219657f1f3-1305336850.tcloudbaseapp.com/photos/%E6%9D%BE%E9%A5%BC.jpg\" />");
//            note11.setGroupId(0);
//            note11.setGroupName("默认笔记");
//            note11.setType(2);
//            note11.setBgColor("#FFFFFF");
//            note11.setIsEncrypt(0);
//            note11.setCreateTime(CommonUtil.date2string(new Date()));
//            noteDao.insertNote(note11);

//            Note note12 = new Note();
//            note12.setTitle("果汁");
//            note12.setContent("<img src=\"https://hello-cloudbase-7gv2as219657f1f3-1305336850.tcloudbaseapp.com/photos/%E6%9E%9C%E6%B1%81.jpg\" />");
//            note12.setGroupId(0);
//            note12.setGroupName("默认笔记");
//            note12.setType(2);
//            note12.setBgColor("#FFFFFF");
//            note12.setIsEncrypt(0);
//            note12.setCreateTime(CommonUtil.date2string(new Date()));
//            noteDao.insertNote(note12);

            Note note13 = new Note();
            note13.setTitle(getResources().getString(R.string.Hamburger));
            note13.setContent("<img src=\"" + URL + "/photos/%E6%B1%89%E5%A0%A1.jpg\" />" +
                    "\nHamburger这个名字起源于德国西北城市汉堡（Hamburg），今日的汉堡是德国最为繁忙的港口，在19世纪中叶的时候，居住在那里的人们喜欢把牛排捣碎成一定形状，这种吃法可能被当时的大量德国移民传到了美洲。\n" +
                    "\n1836年，一道以“汉堡牛排”（Hamburg steak）命名的菜出现在美国人的菜单上；从一本1902年的烹饪手册中我们可以看出，当时Hamburg steak的做法与今天的概念已经很接近了，就是用碎牛肉和洋葱与胡椒粉拌在一起。\n" +
                    "\n到了20世纪晚期，美国人对Hamburg steak的做法进行了改良，然后开车把它送进了快餐店，这就是今天招人喜爱的hamburger的起源。\n" +
                    "\n现代汉堡中除夹传统的牛肉饼外，还在圆面包的第二层中涂以黄油、芥末、番茄酱、沙拉酱等，再夹入番茄片、洋葱、蔬菜、酸黄瓜等食物，就可以同时吃到主副食。\n" +
                    "\n这种食物食用方便、风味可口、营养全面，现在已经成为畅销世界的方便主食之一。因为汉堡包热量高，含有大量脂肪，所以不适合减肥人群或高血压高血脂人群过量食用。\n");
            note13.setGroupId(0);
            note13.setGroupName("默认笔记");
            note13.setType(2);
            note13.setBgColor("#FFFFFF");
            note13.setIsEncrypt(0);
            note13.setCreateTime(CommonUtil.date2string(new Date()));
            noteDao.insertNote(note13);

            Note note14 = new Note();
            note14.setTitle(getResources().getString(R.string.Steak));
            note14.setContent("<img src=\"" + URL + "/photos/%E7%89%9B%E6%8E%92.jpg\" />" +
                    "\n牛排，或称牛扒，是片状的牛肉，是西餐中常见的食物之一。牛排的烹调方法以煎和烤制为主。\n" +
                    "\n欧洲中世纪时，猪肉及羊肉是平民百姓的食用肉，牛肉则是王公贵族们的高级肉品，尊贵的牛肉被他们搭配上了当时也是享有尊贵身份的胡椒粉及香辛料一起烹调，并在特殊场合中供应，以彰显主人的尊贵身份。\n" +
                    "\n清末小说中已出现「牛排」、「猪排」等西菜菜名，可能是因形似上海「大排」（猪丁骨），故名「排」。而在上海话里，「排」发[ba]音，广东又作牛扒。\n" +
                    "\n西方人爱吃较生口味的牛排，由于这种牛排含油适中又略带肉汁，口感甚是鲜美。东方人更偏爱7成熟。\n" +
                    "\n影响牛排口味的因素很多，如食用速度，当牛排上桌后，享用牛排的速度可以决定牛排是否好吃。因为牛排中既有牛油又含汁液，温度如果稍低其牛排的鲜香度会随之降低。\n" +
                    "\n吃牛排讲究火候，而并非享受酥烂口感，这也是在西餐中炖牛肉和煎牛排的区别。另外，餐具也会影响牛排的口味。吃牛排的刀一定要锋利，在吃牛排前一定要先查看一下刀齿是否分明清晰。除此以外，配汁对牛排口味的影响也很大。\n" +
                    "\n在做牛排的时候，如何掌握熟的程度是个功夫活，但是也有简单的方法。英国的Jamie Oliver就提到过，煎牛排的时候如何掌握熟度。很简单，就需要你的一只手：用手指按牛排，感觉牛排的软硬程度。同一只手的大拇指和食指在捏在一起，虎口到大拇指根的软硬程度就是rare steak的感觉；拇指和中指就是medium rare；拇指无名指就是medium，最后小拇指和拇指当然就是well done了。\n");
            note14.setGroupId(0);
            note14.setGroupName("默认笔记");
            note14.setType(2);
            note14.setBgColor("#FFFFFF");
            note14.setIsEncrypt(0);
            note14.setCreateTime(CommonUtil.date2string(new Date()));
            noteDao.insertNote(note14);

//            Note note15 = new Note();
//            note15.setTitle("猪排骨");
//            note15.setContent("<img src=\"https://hello-cloudbase-7gv2as219657f1f3-1305336850.tcloudbaseapp.com/photos/%E7%8C%AA%E6%8E%92%E9%AA%A8.jpg\" />");
//            note15.setGroupId(0);
//            note15.setGroupName("默认笔记");
//            note15.setType(2);
//            note15.setBgColor("#FFFFFF");
//            note15.setIsEncrypt(0);
//            note15.setCreateTime(CommonUtil.date2string(new Date()));
//            noteDao.insertNote(note15);

            Note note16 = new Note();
            note16.setTitle(getResources().getString(R.string.Donut));
            note16.setContent("<img src=\"" + URL + "/photos/%E7%94%9C%E7%94%9C%E5%9C%88.jpg\" />" +
                    "\n甜甜圈在美国是最为受欢迎的一种甜品，任何一个糕点店铺或快餐店都有出售。从5岁儿童到75岁老人都对它有着一致的热爱。在亚洲，甜甜圈主要是被当成点心类的食物，但在美国则有许多人以甜甜圈作为早餐的主食，甚至还设立了“甜甜圈日”。\n" +
                    "\n相比于在冷藏柜里正襟危坐的复杂甜点，甜甜圈显然平易近人得多。一块平平无奇的米黄色面团滑入油锅，立刻爆出滋滋啦啦的响声，欢快地膨胀起来，变成诱人的金黄色。刚出锅的甜甜圈，外形圆润讨巧，油炸过的表面又能完美吸附糖霜、巧克力以及各种甜蜜的奶油酱。趁热咬下一口，糖霜混合黄油的香气迸发出来，面团外脆内软，蓬松中又带点韧性，恰到好处地迎合着牙齿。若是中间能适时溢出一点卡仕达酱，温柔地滑过舌尖，那样缠绵的口感简直美好得犹如初恋。\n" +
                    "\n美国人无疑是甜甜圈的头号粉丝，他们拥有世界上最著名的两大甜甜圈连锁品牌Dunkin' Donuts和 Krispy Kreme。不过甜甜圈的发源地并非美国，在古罗马时期已经出现了油炸面团的做法，中东地区亦有类似甜食，叫做Zalabia。这一做法传入欧洲之后，在英国、德国、北欧等地流行开来，后来又被荷兰人带到美国。\n" +
                    "\n油炸面团最早是具有神秘的宗教意义的。圣经《利未记》中就提到将“调油的无酵饼”油炸后用以献祭的做法。油炸食品的崇高地位很可能得益于油脂的珍贵与稀缺。在古代欧洲，炸甜面团是大斋节前狂欢盛筵上才能享用的食物，或许只有如此高油高糖的甜点，才能提供足够的正能量，帮助人们度过接下来40天的漫长斋戒。\n" +
                    "\n现今可见的最早的甜甜圈配方，见于1803年Sussannah Carter的《The Frugal Housewife》，它以酵母发酵，质地接近于面包。而1830年左右，出现了借助碳酸钾来蓬发的蛋糕甜甜圈，其口感相对紧实，但外皮更为香脆。\n" +
                    "\n甜甜圈的广泛流行，要归功于第一次世界大战。1917年，美国对德宣战，并派遣远征军赴欧洲参战。同时，慈善组织“救世军”征集了一批女性志愿者奔赴前线，为士兵们提供食物和基本的生活服务。为了鼓舞士气，志愿者们用简陋的工具开始制作甜甜圈。这样甜蜜温暖的食物最能抚慰士兵思乡的味蕾，以至于志愿者一天要赶制数千个甜甜圈以满足需求，而她们也因此被士兵亲切地称为“甜甜圈女孩”（Doughnut Girls）。与此同时，在美国境内，救世军还举办大型的甜甜圈义卖活动以筹集善款。\n" +
                    "\n1919年，救世军通过义卖，在短短两天内获得了50万美元的善款。不过，到了二战时期，美国卷入实质性战役，大概是由于物资匮乏的缘故，救世军开始售卖纸质的假冒甜甜圈，赋予其“支援前线战事”的精神意义，完全依赖爱国主义的号召力来拉动销量，就这样，救世军依然通过义卖筹集了将近10万美元。为了纪念甜甜圈在战争中的重要地位，六月的第一个星期五被定为“甜甜圈日”，并传承至今。\n" +
                    "\n不过，最早的甜甜圈其实并没有洞，而是圆滚滚的一坨面团。这样的面团在炸制的时候，很容易出现周边炸过头而中心部分还没熟的情况，因此人们开始在面团中切出一个洞，使其受热更为均匀。\n" +
                    "\n关于甜甜圈之洞的起源，有许多似是而非的版本。据说，在1947年，一位名叫Hanson Gregory的船长在一次远航途中，尝试用胡椒罐的盖子切除甜面团的中心，从而炸出受热均匀的甜甜圈。\n" +
                    "\n另一个更离奇的版本是他让母亲把面团做成圈状，这样可以串在舵轮的辐条上，就算在双手驾驶的时候也可以享用美食。不管怎么说，甜甜圈之洞实在是美食史上一个伟大的里程碑。\n" +
                    "\n它的出现还催生了另一种精巧的小甜食。切割面团时，切下来的小圆片总不能被浪费，扔进油锅里炸一炸，就成了美味的“Doughnut Hole”。它通常是呆萌的圆球状，恰好是一口一个的大小，随意裹上肉桂糖就可以塞进嘴里。虽然没有甜甜圈的华丽外表，这种油炸小球却以其简单纯粹的味道征服了不少人的味蕾。\n");
            note16.setGroupId(0);
            note16.setGroupName("默认笔记");
            note16.setType(2);
            note16.setBgColor("#FFFFFF");
            note16.setIsEncrypt(0);
            note16.setCreateTime(CommonUtil.date2string(new Date()));
            noteDao.insertNote(note16);

//            Note note17 = new Note();
//            note17.setTitle("百吉圈");
//            note17.setContent("<img src=\"https://hello-cloudbase-7gv2as219657f1f3-1305336850.tcloudbaseapp.com/photos/%E7%99%BE%E5%90%89%E5%9C%88.jpg\" />");
//            note17.setGroupId(0);
//            note17.setGroupName("默认笔记");
//            note17.setType(2);
//            note17.setBgColor("#FFFFFF");
//            note17.setIsEncrypt(0);
//            note17.setCreateTime(CommonUtil.date2string(new Date()));
//            noteDao.insertNote(note17);

            Note note18 = new Note();
            note18.setTitle(getResources().getString(R.string.Cake));
            note18.setContent("<img src=\"" + URL + "/photos/%E8%9B%8B%E7%B3%95.jpg\" />" +
                    "\n蛋糕最早起源于西方，后来才慢慢的传入中国。蛋糕是一种古老的西点，一般是由烤箱制作的，最早的蛋糕是用几样简单的材料做出来的。\n" +
                    "\n这些蛋糕是古老宗教神话与奇迹式迷信的象征。早期的经贸路线使异国香料由远东向北输入，坚果、花露水、柑橘类水果、枣子与无花果从中东引进，甘蔗则从东方国家与南方国家进口。\n" +
                    "\n在欧洲黑暗时代，这些珍奇的原料只有僧侣与贵族才能拥有，而他们的糕点创作则是蜂蜜姜饼以及扁平硬饼干之类的东西。慢慢地，随着贸易往来的频繁，西方国家的饮食习惯也跟着彻底地改变。\n" +
                    "\n从十字军东征返家的士兵和阿拉伯商人，把香料的运用和中东的食谱散播。在中欧几个主要的商业重镇，烘焙师傅同业公会也组织了起来。而在中世纪末，香料已被欧洲各地的富有人家广为使用，更增进了想象力丰富的糕点烘焙技术。等到坚果和糖大肆流行时，杏仁糖泥也跟着大众化起来，这种杏仁糖泥是用木雕的凸版模子烤出来的，而模子上的图案则与宗教训诫多有关联。\n");
            note18.setGroupId(0);
            note18.setGroupName("默认笔记");
            note18.setType(2);
            note18.setBgColor("#FFFFFF");
            note18.setIsEncrypt(0);
            note18.setCreateTime(CommonUtil.date2string(new Date()));
            noteDao.insertNote(note18);

            Note note19 = new Note();
            note19.setTitle(getResources().getString(R.string.Chongqing_noodle));
            note19.setContent("<img src=\"" + URL + "/photos/%E9%87%8D%E5%BA%86%E5%B0%8F%E9%9D%A2.jpg\" />" +
                    "\n重庆小面，是重庆四大特色之一，归属于重庆面食的一类发源于重庆，是重庆的主食之一，尤其是早餐较常见。重庆小面是重庆面食中最简单的一种。\n" +
                    "\n重庆小面饮食文化，源于街头，来自街头巷尾的担担面，旧时，小贩扁担挑起箩筐穿梭于民居叫卖的担担面，就是现在小面的雏形，只不过，从小贩的肩头换到了街边搭蓬，再换到了门面，条件好了，作料也日益丰富。\n" +
                    "\n重庆小面是指麻辣素面，麻辣味型。重庆面食还包括重庆小面和有臊子的面食，如牛肉、肥肠、豌豆杂酱面、荣昌铺盖面等。重庆小面富于变化，在面店，可以要求店家制作个人定制口味，如要求店家\"干熘\"(拌面)、\"提黄\"(偏生硬)、\"加青\"(多加蔬菜)、\"重辣\"(多加油辣子)等等。\n" +
                    "\n佐料是重庆小面的灵魂，一碗面条全凭调料提味儿。先调好调料，再放入煮好的面条。麻辣味调和不刺激，面条劲道顺滑，汤料香气扑鼻，味道浓厚。\n" +
                    "\n重庆小面是作为南方人的重庆市民普遍接受的传统面食，因其独特口感，以辣闻名，近年来全国知名。\n");
            note19.setGroupId(0);
            note19.setGroupName("默认笔记");
            note19.setType(2);
            note19.setBgColor("#FFFFFF");
            note19.setIsEncrypt(0);
            note19.setCreateTime(CommonUtil.date2string(new Date()));
            noteDao.insertNote(note19);

            Note note20 = new Note();
            note20.setTitle(getResources().getString(R.string.Bread));
            note20.setContent("<img src=\"" + URL + "/photos/%E9%9D%A2%E5%8C%85.jpg\" />" +
                    "\n面包也写作麺包，又被称为人造果实，是一种用五谷（一般是麦类）磨粉制作并加热而制成的食品。\n" +
                    "\n所谓面包，就是以黑麦、小麦等粮食作物为基本原料，先磨成粉，再加入水、盐、酵母等和面并制成面团坯料，然后再以烘、烤、蒸、煎等方式加热制成的食品。\n" +
                    "\n“埃及奴隶睡着了，发明了面包”。\n" +
                    "\n传说公元前2600年左右，有一个为主人用水和上面粉做饼的埃及奴隶，一天晚上，饼还没有烤好他就睡着了，炉子也灭了。夜里，生面饼开始发酵，膨大了。等到这个奴隶一觉醒来时，生面饼已经比昨晚大了一倍。他连忙把面饼塞回炉子里去，他想这样就不会有人知道他活还没干完就大大咧咧睡着了。饼烤好了，它又松又软。也许是生面饼里的面粉、水或甜味剂（或许就是蜂蜜）暴露在空气里的野生酵母菌或细菌下，当它们经过了一段时间的温暖后，酵母菌生长并传遍了整个面饼。埃及人继续用酵母菌实验，成了世界上第一代职业面包师。\n" +
                    "\n通常，我们提到面包，大多会想到欧美面包或日式的夹馅面包、甜面包等。其实，世界上还有许多特殊种类的面包。世界上广泛使用的制作面包的原料除了黑麦粉、小麦粉以外，还有荞麦粉、糙米粉、玉米粉等。有些面包经酵母发酵，在烘烤过程中变得更加蓬松柔软；还有许多面包恰恰相反，用不着发酵。尽管原料和制作工艺不尽相同，它们都被称为面包。\n");
            note20.setGroupId(0);
            note20.setGroupName("默认笔记");
            note20.setType(2);
            note20.setBgColor("#FFFFFF");
            note20.setIsEncrypt(0);
            note20.setCreateTime(CommonUtil.date2string(new Date()));
            noteDao.insertNote(note20);

            Note note21 = new Note();
            note21.setTitle(getResources().getString(R.string.Dumpling));
            note21.setContent("<img src=\"" + URL + "/photos/%E9%A5%BA%E5%AD%90.jpg\" />" +
                    "\n饺子源于中国古代的角子，原名“娇耳”，汉族传统面食，距今已有一千八百多年的历史。\n" +
                    "\n饺子由馄饨演变而来。在其漫长的发展过程中，名目繁多，古时有“牢丸”“扁食”“饺饵”“粉角”等名称。三国时期称作“月牙馄饨”，南北朝时期称“馄饨”，唐代称饺子为“偃月形馄饨”，宋代称为“角子”，元代、明代称为“扁食”;清代则称为“饺子”。饺子起源于东汉时期，为东汉河南邓州人张仲景首创。当时饺子是药用，张仲景用面皮包上一些祛寒的药材用来治病(羊肉、胡椒等)，避免病人耳朵上生冻疮。\n" +
                    "\n饺子起源于东汉时期，为医圣张仲景首创。饺子多以冷水和面粉为剂，将面和水和在一起，揉成大的粗面团，盖上拯干的湿纱布或毛巾，放置(饧)一小时左右，刀切或手摘成若干个小面团，先后揉搓成直径约3公分左右的圆长条，刀切或手摘成一个个小面剂子，将这些小面剂子用小擀面杖擀成中间略厚周边较薄的饺子皮，包裹馅心，捏成月牙形或角形，先将冷水烧开，包成后下锅并用漏勺或者汤勺(反过来凸面朝上)顺着锅沿逆时针或顺时针划圆弧状以防饺子粘连，煮至饺子浮上水面即可(如为肉馅可在沸腾时添少许冷水再烧，反复两三次)。\n" +
                    "\n汉末三国时期，饺子已经成为一种食品，被称为“月牙馄饨”。魏张揖所著的《广雅》一书中，就提到这种食品。据三国时期魏人张揖著的《广雅》记载那时已有形如月牙称为“馄饨”的食品，和饺子形状基本类似。\n" +
                    "\n到南北朝时，馄饨“形如偃月，天下通食”。据推测，那时的饺子煮熟以后，不是捞出来单独吃，而是和汤一起盛在碗里混着吃，所以当时的人们把饺子叫“馄饨”。这种吃法在中国的一些地区仍然流行，如陕西吃饺子，要在汤里放些香菜、葱花、虾皮、韭菜等小料。\n" +
                    "\n大约到了唐代，饺子已经变得和如今的饺子几乎一样，而且是捞出来放在盘子里单个吃。又称“偃月形馄饨”。\n" +
                    "\n宋代称饺子为“角儿”，它是后世“饺子”一词的词源。宋孟元老《东京梦华录》追忆北宋汴京的繁盛，其卷二曾提到市场上有“水晶角儿”“煎角子”，此外，还有“驼峰角子”。宋四水潜夫周密辑《武林旧事》卷六提到，临安的市场上有“市罗角儿”“诸色角儿”。这种写法，在其后的元、明、清及民国间仍可见到。南宋时叫做“燥肉双下角子”。\n" +
                    "\n饺子在宋代的时候，传入蒙古。饺子传到了蒙古，饺子在蒙古语中读音类似于“匾食”。随着蒙古帝国的征伐，匾食也传到了世界各地。出现了俄罗斯饺子、哈萨克斯坦饺子、朝鲜饺子等多个变种。\n" +
                    "\n根据文献记载，春节时候吃饺子这种习俗最迟在明代已经出现。据《酌中志》载，明代宫廷已是''正月初一五更起……饮柏椒酒，吃水点心(即饺子)。或暗包银钱一二于内，得之者以卜一岁之吉，是日亦互相拜祝,名曰贺新年也。”\n" +
                    "\n清朝时，饺子一般要在年三十晚上子时以前包好，待到半夜子时吃，这时正是农历正月初一的伊始，吃饺子取“更岁交子”之意。“子”为“子时”，交与“饺”谐音，有“喜庆团圆”和“吉祥如意”的意思。清朝有关史料记载：“元旦子时，盛馔同离，如食扁食，名角子，取其更岁交子之义。”又说：“每年初一，无论贫富贵贱，皆以白面做饺食之，谓之煮饽饽，举国皆然，无不同也。富贵之家，暗以金银小锞藏之饽饽中，以卜顺利，家人食得者，则终岁大吉。”这说明新春佳节无论贫富，家家都要吃饺子，寓意吉利，以示辞旧迎新。近人徐珂编的《清稗类钞》中说：“中有馅，或谓之粉角——而蒸食煎食皆可，以水煮之而有汤叫水饺。”“其在正月，则元日至五日为破五，旧例食水饺子五日。”\n" +
                    "\n饺子是一种历史悠久的中国民间吃食，吃饺子也是国人在春节时特有的民俗传统。因为取“更岁交子”之意，所以深受老百姓的欢迎。每逢新春佳节，饺子更成为一种必不可少的佳肴。在许多汉族地区民俗中的 [6]  ，除夕守岁吃“饺子”，是任何山珍海味所无法替代的重头大宴。饺子起源于张仲景的时代，“饺子”又名“交子”或者“娇耳”，是新旧交替之意，也是秉承上苍之意，是必须要吃的一道大宴美食，否则，上苍会在阴阳界中除去你的名字，亡后会变成不在册的孤魂野鬼。表明我国祖先对此的重视度。无论怎样，为除掉一年的晦气您也要在除夕吃一顿“饺子”。远方的人们都会跋山涉水回乡和家人过冬节吃饺子，以示有个圆满的归宿。\n" +
                    "\n饺子的特点是皮薄馅嫩，味道鲜美，形状独特，百食不厌。饺子的制作原料营养素种类齐全，蒸煮法保证营养较少流失，并且符合中国色香味饮食文化的内涵。饺子是一种历史悠久的民间吃食，深受老百姓的欢迎，民间有“好吃不过饺子”的俗语。每逢新春佳节，饺子更成为一种应时不可缺少的佳肴。\n");
            note21.setGroupId(0);
            note21.setGroupName("默认笔记");
            note21.setType(2);
            note21.setBgColor("#FFFFFF");
            note21.setIsEncrypt(0);
            note21.setCreateTime(CommonUtil.date2string(new Date()));
            noteDao.insertNote(note21);

            Note note22 = new Note();
            note22.setTitle(getResources().getString(R.string.Cookie));
            note22.setContent("<img src=\"" + URL + "/photos/%E9%A5%BC%E5%B9%B2.jpg\" />" +
                    "\n饼干（Biscuit）最简单的产品形态是单纯用面粉和水混合的形态，在BC4000年左右古代埃及的古坟中被发现。\n" +
                    "\n而真正成型的饼干，则要追溯到公元七世纪的波斯，当时制糖技术刚刚开发出来，并因为饼干而被广泛使用。一直到了公元十世纪左右，随着穆斯林对西班牙的征服，饼干传到了欧洲，并从此在各个基督教国家中流传。到了公元十四世纪，饼干已经成了全欧洲人最喜欢的点心，从皇室的厨房到平民居住的大街，都弥漫着饼干的香味。现代饼干产业是由19世纪时因发达的航海技术进出于世界各国的英国开始的，在长期的航海中，面包因含有较高的水份（35%-40%）不适合作为储备粮食，所以发明了一种含水份量很低的面包——饼干。\n" +
                    "\n初期饼干的产业是上述所说的长期的航海或战争中的紧急食品的概念开始以HandMade-Type（手工形态）传播，产业革命以后因机械技术的发达，饼干的制作设备及技术迅速发展，扩散到全世界各地。饼干类包含饼干（Biscuit），曲奇饼干（Cookies），苏打饼干（Cracker）和披萨饼干（Pizza）等等。\n" +
                    "\n十九世纪五十年代的一天，法国比斯湾，狂风使一艘英国帆船触礁搁浅，船员死里逃生来到一个荒无人烟的小岛。风停后，人们回到船上找吃的，但船上的面粉、砂糖、奶油全部被水泡了，他们只好把泡在一起的面糊带回岛上，并将它们捏成一个个小团，烤熟后吃。没想到，烤熟的面团又松又脆，味道可口。为了纪念这次脱险，船员们回到英国后，就用同样方法烤制小饼吃，并用海湾的名字“比斯湾”命名这些小饼。这就是今天饼干英文名biscuit的由来。在当地饼干也是一种作为幸运物而存在。\n" +
                    "\n所以，英国的Biscuit和美国的Cracker都是指饼干，其实质相同，只是“口感”上有一点差别。如果吃过“苏打饼干”的人，都会感到它“厚而酥”的是英国式，“薄而脆”的是美国式。而近期流行的“披萨饼干”，也是采用西方披萨外形，增加水果元素，给传统饼干带来创新。\n");
            note22.setGroupId(0);
            note22.setGroupName("默认笔记");
            note22.setType(2);
            note22.setBgColor("#FFFFFF");
            note22.setIsEncrypt(0);
            note22.setCreateTime(CommonUtil.date2string(new Date()));
            noteDao.insertNote(note22);
        }

        noteList = noteDao.queryNotesAll(0);
        mNoteListAdapter.setmNotes(noteList);
        mNoteListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshNoteList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.menu_search);

        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchAutoComplete = (SearchView.SearchAutoComplete) mSearchView.findViewById(R.id.search_src_text);
        mSearchView.setQueryHint(getResources().getString(R.string.search));
        mSearchView.setMaxWidth(600);
        mSearchView.setBackgroundColor(Color.parseColor("#1E8AE8"));

        mSearchAutoComplete.setHintTextColor(Color.parseColor("#999999"));
        mSearchAutoComplete.setTextColor(Color.parseColor("#333333"));
        mSearchAutoComplete.setTextSize(15);
        mSearchAutoComplete.setThreshold(1);

        mSearchView.onActionViewExpanded();
        mSearchView.setIconified(true);

        LinearLayout searchBar = mSearchView.findViewById(R.id.search_bar);
        searchBar.setLayoutTransition(new LayoutTransition());

        LinearLayout search_edit_frame = (LinearLayout) mSearchView.findViewById(R.id.search_edit_frame);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) search_edit_frame.getLayoutParams();
        params.topMargin = 25;
        params.bottomMargin= 25;
        search_edit_frame.setBackgroundColor(Color.parseColor("#1E8AE8"));
        search_edit_frame.setLayoutParams(params);

        View searchViewsCloseBtn = mSearchView.findViewById(R.id.search_close_btn);
        searchViewsCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout search_edit_frame = (LinearLayout) mSearchView.findViewById(R.id.search_edit_frame);
                search_edit_frame.setBackgroundColor(Color.parseColor("#1E8AE8"));

                noteList = noteDao.queryNotesAll(0);
                mNoteListAdapter.setmNotes(noteList);
                mNoteListAdapter.notifyDataSetChanged();

                mSearchView.setQuery("", false);
                mSearchView.onActionViewCollapsed();
                mSearchView.clearFocus();
            }
        });

        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout search_edit_frame = (LinearLayout) mSearchView.findViewById(R.id.search_edit_frame);
                search_edit_frame.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                noteList = noteDao.queryData(s);
                mNoteListAdapter.setmNotes(noteList);
                mNoteListAdapter.notifyDataSetChanged();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
//                noteList = noteDao.queryData(s);
//                mNoteListAdapter.setmNotes(noteList);
//                mNoteListAdapter.notifyDataSetChanged();

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_note:
                Intent intent = new Intent(MainActivity.this, NewActivity.class);
                intent.putExtra("groupName", "默认笔记");
                intent.putExtra("flag", 0);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
