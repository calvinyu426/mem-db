public class TreeNode<T extends RowData> {

    protected T value;

    protected TreeNode<T> left;

    protected TreeNode<T> right;

    public TreeNode(T value) {
        this.value = value;
    }

    protected void insertLeft(T value) {
        this.left = new TreeNode<>(value);
    }

    protected void insertRight(T value) {
        this.right = new TreeNode<>(value);
    }
}
